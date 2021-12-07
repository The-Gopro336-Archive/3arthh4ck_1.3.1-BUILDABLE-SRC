package org.spongepowered.asm.mixin.extensibility;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface IMixinErrorHandler {
    public ErrorAction onPrepareError(IMixinConfig var1, Throwable var2, IMixinInfo var3, ErrorAction var4);

    public ErrorAction onApplyError(String var1, Throwable var2, IMixinInfo var3, ErrorAction var4);

    public static enum ErrorAction {
        NONE(Level.INFO),
        WARN(Level.WARN),
        ERROR(Level.FATAL);

        public final Level logLevel;

        private ErrorAction(Level logLevel) {
            this.logLevel = logLevel;
        }
    }
}
