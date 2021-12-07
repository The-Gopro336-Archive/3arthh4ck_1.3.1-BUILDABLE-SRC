package org.spongepowered.asm.mixin.injection.throwables;

import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;

public class InvalidSliceException
extends InvalidInjectionException {
    private static final long serialVersionUID = 1L;

    public InvalidSliceException(IMixinContext context, String message) {
        super(context, message);
    }

    public InvalidSliceException(ISliceContext owner, String message) {
        super(owner.getContext(), message);
    }

    public InvalidSliceException(IMixinContext context, Throwable cause) {
        super(context, cause);
    }

    public InvalidSliceException(ISliceContext owner, Throwable cause) {
        super(owner.getContext(), cause);
    }

    public InvalidSliceException(IMixinContext context, String message, Throwable cause) {
        super(context, message, cause);
    }

    public InvalidSliceException(ISliceContext owner, String message, Throwable cause) {
        super(owner.getContext(), message, cause);
    }
}
