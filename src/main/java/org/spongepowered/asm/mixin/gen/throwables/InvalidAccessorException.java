package org.spongepowered.asm.mixin.gen.throwables;

import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;

public class InvalidAccessorException
extends InvalidMixinException {
    private static final long serialVersionUID = 2L;
    private final AccessorInfo info;

    public InvalidAccessorException(IMixinContext context, String message) {
        super(context, message);
        this.info = null;
    }

    public InvalidAccessorException(AccessorInfo info, String message) {
        super(info.getContext(), message);
        this.info = info;
    }

    public InvalidAccessorException(IMixinContext context, Throwable cause) {
        super(context, cause);
        this.info = null;
    }

    public InvalidAccessorException(AccessorInfo info, Throwable cause) {
        super(info.getContext(), cause);
        this.info = info;
    }

    public InvalidAccessorException(IMixinContext context, String message, Throwable cause) {
        super(context, message, cause);
        this.info = null;
    }

    public InvalidAccessorException(AccessorInfo info, String message, Throwable cause) {
        super(info.getContext(), message, cause);
        this.info = info;
    }

    public AccessorInfo getAccessorInfo() {
        return this.info;
    }
}
