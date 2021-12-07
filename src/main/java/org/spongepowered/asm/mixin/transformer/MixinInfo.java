package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.lib.tree.InnerClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.InterfaceInfo;
import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorAccessor;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorInterface;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.TargetClassContext;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.mixin.transformer.throwables.MixinReloadException;
import org.spongepowered.asm.mixin.transformer.throwables.MixinTargetAlreadyLoadedException;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.perf.Profiler;

class MixinInfo
implements Comparable<MixinInfo>,
IMixinInfo {
    private static final IMixinService classLoaderUtil = MixinService.getService();
    static int mixinOrder = 0;
    private final transient Logger logger = LogManager.getLogger("mixin");
    private final transient Profiler profiler = MixinEnvironment.getProfiler();
    private final transient MixinConfig parent;
    private final String name;
    private final String className;
    private final int priority;
    private final boolean virtual;
    private final List<ClassInfo> targetClasses;
    private final List<String> targetClassNames;
    private final transient int order = mixinOrder++;
    private final transient IMixinService service;
    private final transient IMixinConfigPlugin plugin;
    private final transient MixinEnvironment.Phase phase;
    private final transient ClassInfo info;
    private final transient SubType type;
    private final transient boolean strict;
    private transient State pendingState;
    private transient State state;

    MixinInfo(IMixinService service, MixinConfig parent, String name, boolean runTransformers, IMixinConfigPlugin plugin, boolean suppressPlugin) {
        this.service = service;
        this.parent = parent;
        this.name = name;
        this.className = parent.getMixinPackage() + name;
        this.plugin = plugin;
        this.phase = parent.getEnvironment().getPhase();
        this.strict = parent.getEnvironment().getOption(MixinEnvironment.Option.DEBUG_TARGETS);
        try {
            byte[] mixinBytes = this.loadMixinClass(this.className, runTransformers);
            this.pendingState = new State(mixinBytes);
            this.info = this.pendingState.getClassInfo();
            this.type = SubType.getTypeFor(this);
        }
        catch (InvalidMixinException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InvalidMixinException((IMixinInfo)this, (Throwable)ex);
        }
        if (!this.type.isLoadable()) {
            classLoaderUtil.registerInvalidClass(this.className);
        }
        try {
            this.priority = this.readPriority(this.pendingState.getClassNode());
            this.virtual = this.readPseudo(this.pendingState.getClassNode());
            this.targetClasses = this.readTargetClasses(this.pendingState.getClassNode(), suppressPlugin);
            this.targetClassNames = Collections.unmodifiableList(Lists.transform(this.targetClasses, Functions.toStringFunction()));
        }
        catch (InvalidMixinException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InvalidMixinException((IMixinInfo)this, (Throwable)ex);
        }
    }

    void validate() {
        if (this.pendingState == null) {
            throw new IllegalStateException("No pending validation state for " + this);
        }
        try {
            this.pendingState.validate(this.type, this.targetClasses);
            this.state = this.pendingState;
        }
        finally {
            this.pendingState = null;
        }
    }

    protected List<ClassInfo> readTargetClasses(MixinClassNode classNode, boolean suppressPlugin) {
        if (classNode == null) {
            return Collections.emptyList();
        }
        AnnotationNode mixin = Annotations.getInvisible(classNode, Mixin.class);
        if (mixin == null) {
            throw new InvalidMixinException((IMixinInfo)this, String.format("The mixin '%s' is missing an @Mixin annotation", this.className));
        }
        ArrayList<ClassInfo> targets = new ArrayList<ClassInfo>();
        List publicTargets = (List)Annotations.getValue(mixin, "value");
        List privateTargets = (List)Annotations.getValue(mixin, "targets");
        if (publicTargets != null) {
            this.readTargets(targets, Lists.transform(publicTargets, new Function<Type, String>(){

                @Override
                public String apply(Type input) {
                    return input.getClassName();
                }
            }), suppressPlugin, false);
        }
        if (privateTargets != null) {
            this.readTargets(targets, Lists.transform(privateTargets, new Function<String, String>(){

                @Override
                public String apply(String input) {
                    return MixinInfo.this.getParent().remapClassName(MixinInfo.this.getClassRef(), input);
                }
            }), suppressPlugin, true);
        }
        return targets;
    }

    private void readTargets(Collection<ClassInfo> outTargets, Collection<String> inTargets, boolean suppressPlugin, boolean checkPublic) {
        for (String targetRef : inTargets) {
            ClassInfo targetInfo;
            String targetName = targetRef.replace('/', '.');
            if (classLoaderUtil.isClassLoaded(targetName) && !this.isReloading()) {
                String message = String.format("Critical problem: %s target %s was already transformed.", this, targetName);
                if (this.parent.isRequired()) {
                    throw new MixinTargetAlreadyLoadedException((IMixinInfo)this, message, targetName);
                }
                this.logger.error(message);
            }
            if (!this.shouldApplyMixin(suppressPlugin, targetName) || (targetInfo = this.getTarget(targetName, checkPublic)) == null || outTargets.contains(targetInfo)) continue;
            outTargets.add(targetInfo);
            targetInfo.addMixin(this);
        }
    }

    private boolean shouldApplyMixin(boolean suppressPlugin, String targetName) {
        Profiler.Section pluginTimer = this.profiler.begin("plugin");
        boolean result = this.plugin == null || suppressPlugin || this.plugin.shouldApplyMixin(targetName, this.className);
        pluginTimer.end();
        return result;
    }

    private ClassInfo getTarget(String targetName, boolean checkPublic) throws InvalidMixinException {
        ClassInfo targetInfo = ClassInfo.forName(targetName);
        if (targetInfo == null) {
            if (this.isVirtual()) {
                this.logger.debug("Skipping virtual target {} for {}", new Object[]{targetName, this});
            } else {
                this.handleTargetError(String.format("@Mixin target %s was not found %s", targetName, this));
            }
            return null;
        }
        this.type.validateTarget(targetName, targetInfo);
        if (checkPublic && targetInfo.isPublic() && !this.isVirtual()) {
            this.handleTargetError(String.format("@Mixin target %s is public in %s and should be specified in value", targetName, this));
        }
        return targetInfo;
    }

    private void handleTargetError(String message) {
        if (this.strict) {
            this.logger.error(message);
            throw new InvalidMixinException((IMixinInfo)this, message);
        }
        this.logger.warn(message);
    }

    protected int readPriority(ClassNode classNode) {
        if (classNode == null) {
            return this.parent.getDefaultMixinPriority();
        }
        AnnotationNode mixin = Annotations.getInvisible(classNode, Mixin.class);
        if (mixin == null) {
            throw new InvalidMixinException((IMixinInfo)this, String.format("The mixin '%s' is missing an @Mixin annotation", this.className));
        }
        Integer priority = (Integer)Annotations.getValue(mixin, "priority");
        return priority == null ? this.parent.getDefaultMixinPriority() : priority.intValue();
    }

    protected boolean readPseudo(ClassNode classNode) {
        return Annotations.getInvisible(classNode, Pseudo.class) != null;
    }

    private boolean isReloading() {
        return this.pendingState instanceof Reloaded;
    }

    private State getState() {
        return this.state != null ? this.state : this.pendingState;
    }

    ClassInfo getClassInfo() {
        return this.info;
    }

    @Override
    public IMixinConfig getConfig() {
        return this.parent;
    }

    MixinConfig getParent() {
        return this.parent;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getClassRef() {
        return this.getClassInfo().getName();
    }

    @Override
    public byte[] getClassBytes() {
        return this.getState().getClassBytes();
    }

    @Override
    public boolean isDetachedSuper() {
        return this.getState().isDetachedSuper();
    }

    public boolean isUnique() {
        return this.getState().isUnique();
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public boolean isAccessor() {
        return this.type instanceof SubType.Accessor;
    }

    public boolean isLoadable() {
        return this.type.isLoadable();
    }

    public Level getLoggingLevel() {
        return this.parent.getLoggingLevel();
    }

    @Override
    public MixinEnvironment.Phase getPhase() {
        return this.phase;
    }

    @Override
    public MixinClassNode getClassNode(int flags) {
        return this.getState().createClassNode(flags);
    }

    @Override
    public List<String> getTargetClasses() {
        return this.targetClassNames;
    }

    List<InterfaceInfo> getSoftImplements() {
        return Collections.unmodifiableList(this.getState().getSoftImplements());
    }

    Set<String> getSyntheticInnerClasses() {
        return Collections.unmodifiableSet(this.getState().getSyntheticInnerClasses());
    }

    Set<String> getInnerClasses() {
        return Collections.unmodifiableSet(this.getState().getInnerClasses());
    }

    List<ClassInfo> getTargets() {
        return Collections.unmodifiableList(this.targetClasses);
    }

    Set<String> getInterfaces() {
        return this.getState().getInterfaces();
    }

    MixinTargetContext createContextFor(TargetClassContext target) {
        MixinClassNode classNode = this.getClassNode(8);
        Profiler.Section preTimer = this.profiler.begin("pre");
        MixinTargetContext preProcessor = this.type.createPreProcessor(classNode).prepare().createContextFor(target);
        preTimer.end();
        return preProcessor;
    }

    private byte[] loadMixinClass(String mixinClassName, boolean runTransformers) throws ClassNotFoundException {
        byte[] mixinBytes = null;
        try {
            String restrictions;
            if (runTransformers && (restrictions = this.service.getClassRestrictions(mixinClassName)).length() > 0) {
                this.logger.error("Classloader restrictions [{}] encountered loading {}, name: {}", new Object[]{restrictions, this, mixinClassName});
            }
            mixinBytes = this.service.getBytecodeProvider().getClassBytes(mixinClassName, runTransformers);
        }
        catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException(String.format("The specified mixin '%s' was not found", mixinClassName));
        }
        catch (IOException ex) {
            this.logger.warn("Failed to load mixin {}, the specified mixin will not be applied", new Object[]{mixinClassName});
            throw new InvalidMixinException(this, "An error was encountered whilst loading the mixin class", (Throwable)ex);
        }
        return mixinBytes;
    }

    void reloadMixin(byte[] mixinBytes) {
        if (this.pendingState != null) {
            throw new IllegalStateException("Cannot reload mixin while it is initialising");
        }
        this.pendingState = new Reloaded(this.state, mixinBytes);
        this.validate();
    }

    @Override
    public int compareTo(MixinInfo other) {
        if (other == null) {
            return 0;
        }
        if (other.priority == this.priority) {
            return this.order - other.order;
        }
        return this.priority - other.priority;
    }

    public void preApply(String transformedName, ClassNode targetClass) {
        if (this.plugin != null) {
            Profiler.Section pluginTimer = this.profiler.begin("plugin");
            this.plugin.preApply(transformedName, targetClass, this.className, this);
            pluginTimer.end();
        }
    }

    public void postApply(String transformedName, ClassNode targetClass) {
        if (this.plugin != null) {
            Profiler.Section pluginTimer = this.profiler.begin("plugin");
            this.plugin.postApply(transformedName, targetClass, this.className, this);
            pluginTimer.end();
        }
        this.parent.postApply(transformedName, targetClass);
    }

    public String toString() {
        return String.format("%s:%s", this.parent.getName(), this.name);
    }

    static abstract class SubType {
        protected final MixinInfo mixin;
        protected final String annotationType;
        protected final boolean targetMustBeInterface;
        protected boolean detached;

        SubType(MixinInfo info, String annotationType, boolean targetMustBeInterface) {
            this.mixin = info;
            this.annotationType = annotationType;
            this.targetMustBeInterface = targetMustBeInterface;
        }

        Collection<String> getInterfaces() {
            return Collections.emptyList();
        }

        boolean isDetachedSuper() {
            return this.detached;
        }

        boolean isLoadable() {
            return false;
        }

        void validateTarget(String targetName, ClassInfo targetInfo) {
            boolean targetIsInterface = targetInfo.isInterface();
            if (targetIsInterface != this.targetMustBeInterface) {
                String not = targetIsInterface ? "" : "not ";
                throw new InvalidMixinException((IMixinInfo)this.mixin, this.annotationType + " target type mismatch: " + targetName + " is " + not + "an interface in " + this);
            }
        }

        abstract void validate(State var1, List<ClassInfo> var2);

        abstract MixinPreProcessorStandard createPreProcessor(MixinClassNode var1);

        static SubType getTypeFor(MixinInfo mixin) {
            if (!mixin.getClassInfo().isInterface()) {
                return new Standard(mixin);
            }
            boolean containsNonAccessorMethod = false;
            for (ClassInfo.Method method : mixin.getClassInfo().getMethods()) {
                containsNonAccessorMethod |= !method.isAccessor();
            }
            if (containsNonAccessorMethod) {
                return new Interface(mixin);
            }
            return new Accessor(mixin);
        }

        static class Accessor
        extends SubType {
            private final Collection<String> interfaces = new ArrayList<String>();

            Accessor(MixinInfo info) {
                super(info, "@Mixin", false);
                this.interfaces.add(info.getClassRef());
            }

            @Override
            boolean isLoadable() {
                return true;
            }

            @Override
            Collection<String> getInterfaces() {
                return this.interfaces;
            }

            @Override
            void validateTarget(String targetName, ClassInfo targetInfo) {
                boolean targetIsInterface = targetInfo.isInterface();
                if (targetIsInterface && !MixinEnvironment.getCompatibilityLevel().supportsMethodsInInterfaces()) {
                    throw new InvalidMixinException((IMixinInfo)this.mixin, "Accessor mixin targetting an interface is not supported in current enviromnment");
                }
            }

            @Override
            void validate(State state, List<ClassInfo> targetClasses) {
                MixinClassNode classNode = state.getClassNode();
                if (!"java/lang/Object".equals(classNode.superName)) {
                    throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class of " + this + " is invalid, found " + classNode.superName.replace('/', '.'));
                }
            }

            @Override
            MixinPreProcessorStandard createPreProcessor(MixinClassNode classNode) {
                return new MixinPreProcessorAccessor(this.mixin, classNode);
            }
        }

        static class Interface
        extends SubType {
            Interface(MixinInfo info) {
                super(info, "@Mixin", true);
            }

            @Override
            void validate(State state, List<ClassInfo> targetClasses) {
                if (!MixinEnvironment.getCompatibilityLevel().supportsMethodsInInterfaces()) {
                    throw new InvalidMixinException((IMixinInfo)this.mixin, "Interface mixin not supported in current enviromnment");
                }
                MixinClassNode classNode = state.getClassNode();
                if (!"java/lang/Object".equals(classNode.superName)) {
                    throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class of " + this + " is invalid, found " + classNode.superName.replace('/', '.'));
                }
            }

            @Override
            MixinPreProcessorStandard createPreProcessor(MixinClassNode classNode) {
                return new MixinPreProcessorInterface(this.mixin, classNode);
            }
        }

        static class Standard
        extends SubType {
            Standard(MixinInfo info) {
                super(info, "@Mixin", false);
            }

            @Override
            void validate(State state, List<ClassInfo> targetClasses) {
                MixinClassNode classNode = state.getClassNode();
                for (ClassInfo targetClass : targetClasses) {
                    if (classNode.superName.equals(targetClass.getSuperName())) continue;
                    if (!targetClass.hasSuperClass(classNode.superName, ClassInfo.Traversal.SUPER)) {
                        ClassInfo superClass = ClassInfo.forName(classNode.superName);
                        if (superClass.isMixin()) {
                            for (ClassInfo superTarget : superClass.getTargets()) {
                                if (!targetClasses.contains(superTarget)) continue;
                                throw new InvalidMixinException((IMixinInfo)this.mixin, "Illegal hierarchy detected. Derived mixin " + this + " targets the same class " + superTarget.getClassName() + " as its superclass " + superClass.getClassName());
                            }
                        }
                        throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class '" + classNode.superName.replace('/', '.') + "' of " + this.mixin.getName() + " was not found in the hierarchy of target class '" + targetClass + "'");
                    }
                    this.detached = true;
                }
            }

            @Override
            MixinPreProcessorStandard createPreProcessor(MixinClassNode classNode) {
                return new MixinPreProcessorStandard(this.mixin, classNode);
            }
        }
    }

    class Reloaded
    extends State {
        private final State previous;

        Reloaded(State previous, byte[] mixinBytes) {
            super(mixinBytes, previous.getClassInfo());
            this.previous = previous;
        }

        @Override
        protected void validateChanges(SubType type, List<ClassInfo> targetClasses) {
            if (!this.syntheticInnerClasses.equals(this.previous.syntheticInnerClasses)) {
                throw new MixinReloadException(MixinInfo.this, "Cannot change inner classes");
            }
            if (!this.interfaces.equals(this.previous.interfaces)) {
                throw new MixinReloadException(MixinInfo.this, "Cannot change interfaces");
            }
            if (!new HashSet(this.softImplements).equals(new HashSet<InterfaceInfo>(this.previous.softImplements))) {
                throw new MixinReloadException(MixinInfo.this, "Cannot change soft interfaces");
            }
            List<ClassInfo> targets = MixinInfo.this.readTargetClasses(this.classNode, true);
            if (!new HashSet<ClassInfo>(targets).equals(new HashSet<ClassInfo>(targetClasses))) {
                throw new MixinReloadException(MixinInfo.this, "Cannot change target classes");
            }
            int priority = MixinInfo.this.readPriority(this.classNode);
            if (priority != MixinInfo.this.getPriority()) {
                throw new MixinReloadException(MixinInfo.this, "Cannot change mixin priority");
            }
        }
    }

    class State {
        private byte[] mixinBytes;
        private final ClassInfo classInfo;
        private boolean detachedSuper;
        private boolean unique;
        protected final Set<String> interfaces = new HashSet<String>();
        protected final List<InterfaceInfo> softImplements = new ArrayList<InterfaceInfo>();
        protected final Set<String> syntheticInnerClasses = new HashSet<String>();
        protected final Set<String> innerClasses = new HashSet<String>();
        protected MixinClassNode classNode;

        State(byte[] mixinBytes) {
            this(mixinBytes, null);
        }

        State(byte[] mixinBytes, ClassInfo classInfo) {
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

        MixinClassNode getClassNode() {
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

        MixinClassNode createClassNode(int flags) {
            MixinClassNode classNode = new MixinClassNode(MixinInfo.this);
            ClassReader classReader = new ClassReader(this.mixinBytes);
            classReader.accept(classNode, flags);
            return classNode;
        }

        void validate(SubType type, List<ClassInfo> targetClasses) {
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

        void readImplementations(SubType type) {
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
                if (this instanceof Reloaded) continue;
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

        protected void validateChanges(SubType type, List<ClassInfo> targetClasses) {
            type.createPreProcessor(this.classNode).prepare();
        }
    }

    class MixinClassNode
    extends ClassNode {
        public final List<MixinMethodNode> mixinMethods;

        public MixinClassNode(MixinInfo mixin) {
            this(327680);
        }

        public MixinClassNode(int api) {
            super(api);
            this.mixinMethods = this.methods;
        }

        public MixinInfo getMixin() {
            return MixinInfo.this;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MixinMethodNode method = new MixinMethodNode(access, name, desc, signature, exceptions);
            this.methods.add(method);
            return method;
        }
    }

    class MixinMethodNode
    extends MethodNode {
        private final String originalName;

        public MixinMethodNode(int access, String name, String desc, String signature, String[] exceptions) {
            super(327680, access, name, desc, signature, exceptions);
            this.originalName = name;
        }

        public String toString() {
            return String.format("%s%s", this.originalName, this.desc);
        }

        public String getOriginalName() {
            return this.originalName;
        }

        public boolean isInjector() {
            return this.getInjectorAnnotation() != null || this.isSurrogate();
        }

        public boolean isSurrogate() {
            return this.getVisibleAnnotation(Surrogate.class) != null;
        }

        public boolean isSynthetic() {
            return Bytecode.hasFlag(this, 4096);
        }

        public AnnotationNode getVisibleAnnotation(Class<? extends Annotation> annotationClass) {
            return Annotations.getVisible(this, annotationClass);
        }

        public AnnotationNode getInjectorAnnotation() {
            return InjectionInfo.getInjectorAnnotation(MixinInfo.this, this);
        }

        public IMixinInfo getOwner() {
            return MixinInfo.this;
        }
    }
}
