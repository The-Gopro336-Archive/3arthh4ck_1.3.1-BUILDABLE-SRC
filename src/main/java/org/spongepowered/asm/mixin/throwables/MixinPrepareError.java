package org.spongepowered.asm.mixin.throwables;

public class MixinPrepareError
extends Error {
    private static final long serialVersionUID = 1L;

    public MixinPrepareError(String message) {
        super(message);
    }

    public MixinPrepareError(Throwable cause) {
        super(cause);
    }

    public MixinPrepareError(String message, Throwable cause) {
        super(message, cause);
    }
}
