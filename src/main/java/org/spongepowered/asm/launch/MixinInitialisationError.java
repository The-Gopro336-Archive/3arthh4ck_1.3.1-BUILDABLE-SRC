package org.spongepowered.asm.launch;

public class MixinInitialisationError
extends Error {
    private static final long serialVersionUID = 1L;

    public MixinInitialisationError() {
    }

    public MixinInitialisationError(String message) {
        super(message);
    }

    public MixinInitialisationError(Throwable cause) {
        super(cause);
    }

    public MixinInitialisationError(String message, Throwable cause) {
        super(message, cause);
    }
}
