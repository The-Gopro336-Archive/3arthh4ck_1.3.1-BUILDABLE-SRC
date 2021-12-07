package org.spongepowered.asm.mixin.throwables;

public class MixinApplyError
extends Error {
    private static final long serialVersionUID = 1L;

    public MixinApplyError(String message) {
        super(message);
    }

    public MixinApplyError(Throwable cause) {
        super(cause);
    }

    public MixinApplyError(String message, Throwable cause) {
        super(message, cause);
    }
}
