package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorInterface;

class MixinPreProcessorAccessor
extends MixinPreProcessorInterface {
    public MixinPreProcessorAccessor(MixinInfo mixin, MixinInfo.MixinClassNode classNode) {
        super(mixin, classNode);
    }
}
