/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.LoggingFacade;
import java.util.logging.Level;
import java.util.logging.Logger;

class LoggingFacadeImpl
implements LoggingFacade {
    private static final Logger LOG = Logger.getLogger(FlatLaf.class.getName());

    LoggingFacadeImpl() {
    }

    @Override
    public void logSevere(String message, Throwable t) {
        LOG.log(Level.SEVERE, message, t);
    }

    @Override
    public void logConfig(String message, Throwable t) {
        LOG.log(Level.CONFIG, message, t);
    }
}

