package org.spongepowered.asm.mixin.extensibility;

import org.apache.logging.log4j.Level;

public enum IMixinErrorHandler$ErrorAction {
    NONE(Level.INFO),
    WARN(Level.WARN),
    ERROR(Level.FATAL);

    public final Level logLevel;

    private IMixinErrorHandler$ErrorAction(Level logLevel) {
        this.logLevel = logLevel;
    }
}
