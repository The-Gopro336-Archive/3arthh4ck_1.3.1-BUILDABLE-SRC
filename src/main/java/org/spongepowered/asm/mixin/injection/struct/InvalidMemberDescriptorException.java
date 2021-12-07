package org.spongepowered.asm.mixin.injection.struct;

import org.spongepowered.asm.mixin.throwables.MixinException;

public class InvalidMemberDescriptorException
extends MixinException {
    private static final long serialVersionUID = 1L;

    public InvalidMemberDescriptorException(String message) {
        super(message);
    }

    public InvalidMemberDescriptorException(Throwable cause) {
        super(cause);
    }

    public InvalidMemberDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }
}
