package org.spongepowered.asm.mixin.transformer;

import java.io.OutputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.struct.SourceMap;
import org.spongepowered.asm.mixin.transformer.ClassContext;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinApplicatorInterface;
import org.spongepowered.asm.mixin.transformer.MixinApplicatorStandard;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.ClassSignature;

class TargetClassContext
extends ClassContext
implements ITargetClassContext {
    private static final Logger logger = LogManager.getLogger("mixin");
    private final MixinEnvironment env;
    private final Extensions extensions;
    private final String sessionId;
    private final String className;
    private final ClassNode classNode;
    private final ClassInfo classInfo;
    private final SourceMap sourceMap;
    private final ClassSignature signature;
    private final SortedSet<MixinInfo> mixins;
    private final Map<String, Target> targetMethods = new HashMap<String, Target>();
    private final Set<MethodNode> mixinMethods = new HashSet<MethodNode>();
    private int nextUniqueMethodIndex;
    private int nextUniqueFieldIndex;
    private boolean applied;
    private boolean forceExport;

    TargetClassContext(MixinEnvironment env, Extensions extensions, String sessionId, String name, ClassNode classNode, SortedSet<MixinInfo> mixins) {
        this.env = env;
        this.extensions = extensions;
        this.sessionId = sessionId;
        this.className = name;
        this.classNode = classNode;
        this.classInfo = ClassInfo.fromClassNode(classNode);
        this.signature = this.classInfo.getSignature();
        this.mixins = mixins;
        this.sourceMap = new SourceMap(classNode.sourceFile);
        this.sourceMap.addFile(this.classNode);
    }

    public String toString() {
        return this.className;
    }

    boolean isApplied() {
        return this.applied;
    }

    boolean isExportForced() {
        return this.forceExport;
    }

    Extensions getExtensions() {
        return this.extensions;
    }

    String getSessionId() {
        return this.sessionId;
    }

    @Override
    String getClassRef() {
        return this.classNode.name;
    }

    String getClassName() {
        return this.className;
    }

    @Override
    public ClassNode getClassNode() {
        return this.classNode;
    }

    List<MethodNode> getMethods() {
        return this.classNode.methods;
    }

    List<FieldNode> getFields() {
        return this.classNode.fields;
    }

    @Override
    public ClassInfo getClassInfo() {
        return this.classInfo;
    }

    SortedSet<MixinInfo> getMixins() {
        return this.mixins;
    }

    SourceMap getSourceMap() {
        return this.sourceMap;
    }

    void mergeSignature(ClassSignature signature) {
        this.signature.merge(signature);
    }

    void addMixinMethod(MethodNode method) {
        this.mixinMethods.add(method);
    }

    void methodMerged(MethodNode method) {
        if (!this.mixinMethods.remove(method)) {
            logger.debug("Unexpected: Merged unregistered method {}{} in {}", new Object[]{method.name, method.desc, this});
        }
    }

    MethodNode findMethod(Deque<String> aliases, String desc) {
        return this.findAliasedMethod(aliases, desc, true);
    }

    MethodNode findAliasedMethod(Deque<String> aliases, String desc) {
        return this.findAliasedMethod(aliases, desc, false);
    }

    private MethodNode findAliasedMethod(Deque<String> aliases, String desc, boolean includeMixinMethods) {
        String alias = aliases.poll();
        if (alias == null) {
            return null;
        }
        for (MethodNode target : this.classNode.methods) {
            if (!target.name.equals(alias) || !target.desc.equals(desc)) continue;
            return target;
        }
        if (includeMixinMethods) {
            for (MethodNode target : this.mixinMethods) {
                if (!target.name.equals(alias) || !target.desc.equals(desc)) continue;
                return target;
            }
        }
        return this.findAliasedMethod(aliases, desc);
    }

    FieldNode findAliasedField(Deque<String> aliases, String desc) {
        String alias = aliases.poll();
        if (alias == null) {
            return null;
        }
        for (FieldNode target : this.classNode.fields) {
            if (!target.name.equals(alias) || !target.desc.equals(desc)) continue;
            return target;
        }
        return this.findAliasedField(aliases, desc);
    }

    Target getTargetMethod(MethodNode method) {
        if (!this.classNode.methods.contains(method)) {
            throw new IllegalArgumentException("Invalid target method supplied to getTargetMethod()");
        }
        String targetName = method.name + method.desc;
        Target target = this.targetMethods.get(targetName);
        if (target == null) {
            target = new Target(this.classNode, method);
            this.targetMethods.put(targetName, target);
        }
        return target;
    }

    String getUniqueName(MethodNode method, boolean preservePrefix) {
        String uniqueIndex = Integer.toHexString(this.nextUniqueMethodIndex++);
        String pattern = preservePrefix ? "%2$s_$md$%1$s$%3$s" : "md%s$%s$%s";
        return String.format(pattern, this.sessionId.substring(30), method.name, uniqueIndex);
    }

    String getUniqueName(FieldNode field) {
        String uniqueIndex = Integer.toHexString(this.nextUniqueFieldIndex++);
        return String.format("fd%s$%s$%s", this.sessionId.substring(30), field.name, uniqueIndex);
    }

    void applyMixins() {
        if (this.applied) {
            throw new IllegalStateException("Mixins already applied to target class " + this.className);
        }
        this.applied = true;
        MixinApplicatorStandard applicator = this.createApplicator();
        applicator.apply(this.mixins);
        this.applySignature();
        this.upgradeMethods();
        this.checkMerges();
    }

    private MixinApplicatorStandard createApplicator() {
        if (this.classInfo.isInterface()) {
            return new MixinApplicatorInterface(this);
        }
        return new MixinApplicatorStandard(this);
    }

    private void applySignature() {
        this.getClassNode().signature = this.signature.toString();
    }

    private void checkMerges() {
        for (MethodNode method : this.mixinMethods) {
            if (method.name.startsWith("<")) continue;
            logger.debug("Unexpected: Registered method {}{} in {} was not merged", new Object[]{method.name, method.desc, this});
        }
    }

    void processDebugTasks() {
        if (!this.env.getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
            return;
        }
        AnnotationNode classDebugAnnotation = Annotations.getVisible(this.classNode, Debug.class);
        if (classDebugAnnotation != null) {
            this.forceExport = Boolean.TRUE.equals(Annotations.getValue(classDebugAnnotation, "export"));
            if (Boolean.TRUE.equals(Annotations.getValue(classDebugAnnotation, "print"))) {
                Bytecode.textify(this.classNode, (OutputStream)System.err);
            }
        }
        for (MethodNode method : this.classNode.methods) {
            AnnotationNode methodDebugAnnotation = Annotations.getVisible(method, Debug.class);
            if (methodDebugAnnotation == null || !Boolean.TRUE.equals(Annotations.getValue(methodDebugAnnotation, "print"))) continue;
            Bytecode.textify(method, (OutputStream)System.err);
        }
    }
}
