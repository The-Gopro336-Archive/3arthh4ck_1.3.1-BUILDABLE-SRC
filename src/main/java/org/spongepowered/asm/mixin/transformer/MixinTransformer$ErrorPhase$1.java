package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

final class MixinTransformer$ErrorPhase$1
extends MixinTransformer.ErrorPhase {
    @Override
    IMixinErrorHandler.ErrorAction onError(IMixinErrorHandler handler, String context, InvalidMixinException ex, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action) {
        try {
            return handler.onPrepareError(mixin.getConfig(), ex, mixin, action);
        }
        catch (AbstractMethodError ame) {
            return action;
        }
    }

    @Override
    protected String getContext(IMixinInfo mixin, String context) {
        return String.format("preparing %s in %s", mixin.getName(), context);
    }
}
