package org.spongepowered.asm.mixin.transformer.throwables;

import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

public class InvalidInterfaceMixinException
extends InvalidMixinException {
    private static final long serialVersionUID = 2L;

    public InvalidInterfaceMixinException(IMixinInfo mixin, String message) {
        super(mixin, message);
    }

    public InvalidInterfaceMixinException(IMixinContext context, String message) {
        super(context, message);
    }

    public InvalidInterfaceMixinException(IMixinInfo mixin, Throwable cause) {
        super(mixin, cause);
    }

    public InvalidInterfaceMixinException(IMixinContext context, Throwable cause) {
        super(context, cause);
    }

    public InvalidInterfaceMixinException(IMixinInfo mixin, String message, Throwable cause) {
        super(mixin, message, cause);
    }

    public InvalidInterfaceMixinException(IMixinContext context, String message, Throwable cause) {
        super(context, message, cause);
    }
}
