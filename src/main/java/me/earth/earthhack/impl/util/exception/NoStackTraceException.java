/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.exception;

public class NoStackTraceException
extends Exception {
    public NoStackTraceException(String message) {
        super(message);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }
}

