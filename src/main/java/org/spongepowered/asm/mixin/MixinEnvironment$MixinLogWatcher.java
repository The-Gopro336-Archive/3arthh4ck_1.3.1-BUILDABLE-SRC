package org.spongepowered.asm.mixin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.spongepowered.asm.mixin.MixinEnvironment;

class MixinEnvironment$MixinLogWatcher {
    static MixinAppender appender = new MixinAppender();
    static org.apache.logging.log4j.core.Logger log;
    static Level oldLevel;

    MixinEnvironment$MixinLogWatcher() {
    }

    static void begin() {
        Logger fmlLog = LogManager.getLogger("FML");
        if (!(fmlLog instanceof org.apache.logging.log4j.core.Logger)) {
            return;
        }
        log = (org.apache.logging.log4j.core.Logger)fmlLog;
        oldLevel = log.getLevel();
        appender.start();
        log.addAppender((Appender)appender);
        log.setLevel(Level.ALL);
    }

    static void end() {
        if (log != null) {
            log.removeAppender((Appender)appender);
        }
    }

    static {
        oldLevel = null;
    }

    static class MixinAppender
    extends AbstractAppender {
        MixinAppender() {
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
}
