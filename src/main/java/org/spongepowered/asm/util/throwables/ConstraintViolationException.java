package org.spongepowered.asm.util.throwables;

import org.spongepowered.asm.util.ConstraintParser;

public class ConstraintViolationException
extends Exception {
    private static final String MISSING_VALUE = "UNRESOLVED";
    private static final long serialVersionUID = 1L;
    private final ConstraintParser.Constraint constraint;
    private final String badValue;

    public ConstraintViolationException(ConstraintParser.Constraint constraint) {
        this.constraint = constraint;
        this.badValue = MISSING_VALUE;
    }

    public ConstraintViolationException(ConstraintParser.Constraint constraint, int badValue) {
        this.constraint = constraint;
        this.badValue = String.valueOf(badValue);
    }

    public ConstraintViolationException(String message, ConstraintParser.Constraint constraint) {
        super(message);
        this.constraint = constraint;
        this.badValue = MISSING_VALUE;
    }

    public ConstraintViolationException(String message, ConstraintParser.Constraint constraint, int badValue) {
        super(message);
        this.constraint = constraint;
        this.badValue = String.valueOf(badValue);
    }

    public ConstraintViolationException(Throwable cause, ConstraintParser.Constraint constraint) {
        super(cause);
        this.constraint = constraint;
        this.badValue = MISSING_VALUE;
    }

    public ConstraintViolationException(Throwable cause, ConstraintParser.Constraint constraint, int badValue) {
        super(cause);
        this.constraint = constraint;
        this.badValue = String.valueOf(badValue);
    }

    public ConstraintViolationException(String message, Throwable cause, ConstraintParser.Constraint constraint) {
        super(message, cause);
        this.constraint = constraint;
        this.badValue = MISSING_VALUE;
    }

    public ConstraintViolationException(String message, Throwable cause, ConstraintParser.Constraint constraint, int badValue) {
        super(message, cause);
        this.constraint = constraint;
        this.badValue = String.valueOf(badValue);
    }

    public ConstraintParser.Constraint getConstraint() {
        return this.constraint;
    }

    public String getBadValue() {
        return this.badValue;
    }
}
