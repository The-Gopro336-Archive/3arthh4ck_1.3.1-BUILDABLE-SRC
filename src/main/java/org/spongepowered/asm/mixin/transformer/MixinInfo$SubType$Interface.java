package org.spongepowered.asm.mixin.transformer;

import java.util.List;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorInterface;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

class MixinInfo$SubType$Interface
extends MixinInfo.SubType {
    MixinInfo$SubType$Interface(MixinInfo info) {
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
