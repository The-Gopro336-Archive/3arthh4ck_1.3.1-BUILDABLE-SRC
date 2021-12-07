package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.InnerClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.InterfaceInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.util.Annotations;

class MixinInfo$State {
    private byte[] mixinBytes;
    private final ClassInfo classInfo;
    private boolean detachedSuper;
    private boolean unique;
    protected final Set<String> interfaces = new HashSet<String>();
    protected final List<InterfaceInfo> softImplements = new ArrayList<InterfaceInfo>();
    protected final Set<String> syntheticInnerClasses = new HashSet<String>();
    protected final Set<String> innerClasses = new HashSet<String>();
    protected MixinInfo.MixinClassNode classNode;

    MixinInfo$State(byte[] mixinBytes) {
        this(mixinBytes, null);
    }

    MixinInfo$State(byte[] mixinBytes, ClassInfo classInfo) {
        this.mixinBytes = mixinBytes;
        this.connect();
        this.classInfo = classInfo != null ? classInfo : ClassInfo.fromClassNode(this.getClassNode());
    }

    private void connect() {
        this.classNode = this.createClassNode(0);
    }

    private void complete() {
        this.classNode = null;
    }

    ClassInfo getClassInfo() {
        return this.classInfo;
    }

    byte[] getClassBytes() {
        return this.mixinBytes;
    }

    MixinInfo.MixinClassNode getClassNode() {
        return this.classNode;
    }

    boolean isDetachedSuper() {
        return this.detachedSuper;
    }

    boolean isUnique() {
        return this.unique;
    }

    List<? extends InterfaceInfo> getSoftImplements() {
        return this.softImplements;
    }

    Set<String> getSyntheticInnerClasses() {
        return this.syntheticInnerClasses;
    }

    Set<String> getInnerClasses() {
        return this.innerClasses;
    }

    Set<String> getInterfaces() {
        return this.interfaces;
    }

    MixinInfo.MixinClassNode createClassNode(int flags) {
        MixinInfo.MixinClassNode classNode = new MixinInfo.MixinClassNode(MixinInfo.this, MixinInfo.this);
        ClassReader classReader = new ClassReader(this.mixinBytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    void validate(MixinInfo.SubType type, List<ClassInfo> targetClasses) {
        MixinPreProcessorStandard preProcessor = type.createPreProcessor(this.getClassNode()).prepare();
        for (ClassInfo target : targetClasses) {
            preProcessor.conform(target);
        }
        type.validate(this, targetClasses);
        this.detachedSuper = type.isDetachedSuper();
        this.unique = Annotations.getVisible(this.getClassNode(), Unique.class) != null;
        this.validateInner();
        this.validateClassVersion();
        this.validateRemappables(targetClasses);
        this.readImplementations(type);
        this.readInnerClasses();
        this.validateChanges(type, targetClasses);
        this.complete();
    }

    private void validateInner() {
        if (!this.classInfo.isProbablyStatic()) {
            throw new InvalidMixinException((IMixinInfo)MixinInfo.this, "Inner class mixin must be declared static");
        }
    }

    private void validateClassVersion() {
        if (this.classNode.version > MixinEnvironment.getCompatibilityLevel().classVersion()) {
            String helpText = ".";
            for (MixinEnvironment.CompatibilityLevel level : MixinEnvironment.CompatibilityLevel.values()) {
                if (level.classVersion() < this.classNode.version) continue;
                helpText = String.format(". Mixin requires compatibility level %s or above.", level.name());
            }
            throw new InvalidMixinException((IMixinInfo)MixinInfo.this, "Unsupported mixin class version " + this.classNode.version + helpText);
        }
    }

    private void validateRemappables(List<ClassInfo> targetClasses) {
        if (targetClasses.size() > 1) {
            for (FieldNode field : this.classNode.fields) {
                this.validateRemappable(Shadow.class, field.name, Annotations.getVisible(field, Shadow.class));
            }
            for (MethodNode method : this.classNode.methods) {
                this.validateRemappable(Shadow.class, method.name, Annotations.getVisible(method, Shadow.class));
                AnnotationNode overwrite = Annotations.getVisible(method, Overwrite.class);
                if (overwrite == null || (method.access & 8) != 0 && (method.access & 1) != 0) continue;
                throw new InvalidMixinException((IMixinInfo)MixinInfo.this, "Found @Overwrite annotation on " + method.name + " in " + MixinInfo.this);
            }
        }
    }

    private void validateRemappable(Class<Shadow> annotationClass, String name, AnnotationNode annotation) {
        if (annotation != null && Annotations.getValue(annotation, "remap", Boolean.TRUE).booleanValue()) {
            throw new InvalidMixinException((IMixinInfo)MixinInfo.this, "Found a remappable @" + annotationClass.getSimpleName() + " annotation on " + name + " in " + this);
        }
    }

    void readImplementations(MixinInfo.SubType type) {
        this.interfaces.addAll(this.classNode.interfaces);
        this.interfaces.addAll(type.getInterfaces());
        AnnotationNode implementsAnnotation = Annotations.getInvisible(this.classNode, Implements.class);
        if (implementsAnnotation == null) {
            return;
        }
        List interfaces = (List)Annotations.getValue(implementsAnnotation);
        if (interfaces == null) {
            return;
        }
        for (AnnotationNode interfaceNode : interfaces) {
            InterfaceInfo interfaceInfo = InterfaceInfo.fromAnnotation(MixinInfo.this, interfaceNode);
            this.softImplements.add(interfaceInfo);
            this.interfaces.add(interfaceInfo.getInternalName());
            if (this instanceof MixinInfo.Reloaded) continue;
            this.classInfo.addInterface(interfaceInfo.getInternalName());
        }
    }

    void readInnerClasses() {
        for (InnerClassNode inner : this.classNode.innerClasses) {
            ClassInfo innerClass = ClassInfo.forName(inner.name);
            if ((inner.outerName == null || !inner.outerName.equals(this.classInfo.getName())) && !inner.name.startsWith(this.classNode.name + "$")) continue;
            if (innerClass.isProbablyStatic() && innerClass.isSynthetic()) {
                this.syntheticInnerClasses.add(inner.name);
                continue;
            }
            this.innerClasses.add(inner.name);
        }
    }

    protected void validateChanges(MixinInfo.SubType type, List<ClassInfo> targetClasses) {
        type.createPreProcessor(this.classNode).prepare();
    }
}
