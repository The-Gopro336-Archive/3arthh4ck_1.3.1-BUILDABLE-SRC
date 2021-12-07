package org.spongepowered.asm.mixin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.spongepowered.asm.mixin.MixinEnvironment;

class MixinEnvironment$MixinLogWatcher$MixinAppender
extends AbstractAppender {
    MixinEnvironment$MixinLogWatcher$MixinAppender() {
        super("MixinLogWatcherAppender", null, null);
    }

    public void append(LogEvent event) {
        if (event.getLevel() != Level.DEBUG || !"Validating minecraft".equals(event.getMessage().getFormattedMessage())) {
            return;
        }
        MixinEnvironment.gotoPhase(MixinEnvironment.Phase.INIT);
        if (log.getLevel() == Level.ALL) {
            log.setLevel(oldLevel);
        }
    }
}
