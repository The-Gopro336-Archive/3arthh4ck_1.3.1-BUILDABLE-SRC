package org.spongepowered.asm.mixin.transformer.throwables;

import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

public class MixinTargetAlreadyLoadedException
extends InvalidMixinException {
    private static final long serialVersionUID = 1L;
    private final String target;

    public MixinTargetAlreadyLoadedException(IMixinInfo mixin, String message, String target) {
        super(mixin, message);
        this.target = target;
    }

    public MixinTargetAlreadyLoadedException(IMixinInfo mixin, String message, String target, Throwable cause) {
        super(mixin, message, cause);
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }
}
