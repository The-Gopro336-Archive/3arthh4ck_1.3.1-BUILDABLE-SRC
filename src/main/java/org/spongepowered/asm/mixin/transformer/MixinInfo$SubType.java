package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorAccessor;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorInterface;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

abstract class MixinInfo$SubType {
    protected final MixinInfo mixin;
    protected final String annotationType;
    protected final boolean targetMustBeInterface;
    protected boolean detached;

    MixinInfo$SubType(MixinInfo info, String annotationType, boolean targetMustBeInterface) {
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

    abstract void validate(MixinInfo.State var1, List<ClassInfo> var2);

    abstract MixinPreProcessorStandard createPreProcessor(MixinInfo.MixinClassNode var1);

    static MixinInfo$SubType getTypeFor(MixinInfo mixin) {
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
    extends MixinInfo$SubType {
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
        void validate(MixinInfo.State state, List<ClassInfo> targetClasses) {
            MixinInfo.MixinClassNode classNode = state.getClassNode();
            if (!"java/lang/Object".equals(classNode.superName)) {
                throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class of " + this + " is invalid, found " + classNode.superName.replace('/', '.'));
            }
        }

        @Override
        MixinPreProcessorStandard createPreProcessor(MixinInfo.MixinClassNode classNode) {
            return new MixinPreProcessorAccessor(this.mixin, classNode);
        }
    }

    static class Interface
    extends MixinInfo$SubType {
        Interface(MixinInfo info) {
            super(info, "@Mixin", true);
        }

        @Override
        void validate(MixinInfo.State state, List<ClassInfo> targetClasses) {
            if (!MixinEnvironment.getCompatibilityLevel().supportsMethodsInInterfaces()) {
                throw new InvalidMixinException((IMixinInfo)this.mixin, "Interface mixin not supported in current enviromnment");
            }
            MixinInfo.MixinClassNode classNode = state.getClassNode();
            if (!"java/lang/Object".equals(classNode.superName)) {
                throw new InvalidMixinException((IMixinInfo)this.mixin, "Super class of " + this + " is invalid, found " + classNode.superName.replace('/', '.'));
            }
        }

        @Override
        MixinPreProcessorStandard createPreProcessor(MixinInfo.MixinClassNode classNode) {
            return new MixinPreProcessorInterface(this.mixin, classNode);
        }
    }

    static class Standard
    extends MixinInfo$SubType {
        Standard(MixinInfo info) {
            super(info, "@Mixin", false);
        }

        @Override
        void validate(MixinInfo.State state, List<ClassInfo> targetClasses) {
            MixinInfo.MixinClassNode classNode = state.getClassNode();
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
        MixinPreProcessorStandard createPreProcessor(MixinInfo.MixinClassNode classNode) {
            return new MixinPreProcessorStandard(this.mixin, classNode);
        }
    }
}
