package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IHotSwap;

class MixinTransformer$1
implements MixinConfig.IListener {
    final IHotSwap val$hotSwapper;

    MixinTransformer$1(IHotSwap iHotSwap) {
        this.val$hotSwapper = iHotSwap;
    }

    @Override
    public void onPrepare(MixinInfo mixin) {
        this.val$hotSwapper.registerMixinClass(mixin.getClassName());
    }

    @Override
    public void onInit(MixinInfo mixin) {
    }
}
