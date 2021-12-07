package org.spongepowered.asm.mixin.transformer.throwables;

public class MixinTransformerError
extends Error {
    private static final long serialVersionUID = 1L;

    public MixinTransformerError(String message) {
        super(message);
    }

    public MixinTransformerError(Throwable cause) {
        super(cause);
    }

    public MixinTransformerError(String message, Throwable cause) {
        super(message, cause);
    }
}
