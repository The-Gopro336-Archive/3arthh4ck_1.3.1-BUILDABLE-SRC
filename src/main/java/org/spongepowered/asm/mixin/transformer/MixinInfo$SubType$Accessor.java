package org.spongepowered.asm.mixin.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorAccessor;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

class MixinInfo$SubType$Accessor
extends MixinInfo.SubType {
    private final Collection<String> interfaces = new ArrayList<String>();

    MixinInfo$SubType$Accessor(MixinInfo info) {
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
