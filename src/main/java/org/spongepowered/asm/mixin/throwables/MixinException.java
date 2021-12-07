/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.mixin.throwables;

public class MixinException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MixinException() {
    }

    public MixinException(String message) {
        super(message);
    }

    public MixinException(Throwable cause) {
        super(cause);
    }

    public MixinException(String message, Throwable cause) {
        super(message, cause);
    }
}

