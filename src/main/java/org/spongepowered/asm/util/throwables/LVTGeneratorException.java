package org.spongepowered.asm.util.throwables;

import org.spongepowered.asm.mixin.throwables.MixinException;

public class LVTGeneratorException
extends MixinException {
    private static final long serialVersionUID = 1L;

    public LVTGeneratorException(String message) {
        super(message);
    }

    public LVTGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
