package org.spongepowered.asm.util.throwables;

public class InvalidConstraintException
extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidConstraintException() {
    }

    public InvalidConstraintException(String s) {
        super(s);
    }

    public InvalidConstraintException(Throwable cause) {
        super(cause);
    }

    public InvalidConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}
