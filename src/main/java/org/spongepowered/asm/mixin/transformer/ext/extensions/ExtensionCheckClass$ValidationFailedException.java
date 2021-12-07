package org.spongepowered.asm.mixin.transformer.ext.extensions;

import org.spongepowered.asm.mixin.throwables.MixinException;

public class ExtensionCheckClass$ValidationFailedException
extends MixinException {
    private static final long serialVersionUID = 1L;

    public ExtensionCheckClass$ValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionCheckClass$ValidationFailedException(String message) {
        super(message);
    }

    public ExtensionCheckClass$ValidationFailedException(Throwable cause) {
        super(cause);
    }
}
