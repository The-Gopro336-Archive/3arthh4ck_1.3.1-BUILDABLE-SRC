/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.newdawn.slick.util.DefaultLogSystem;
import org.newdawn.slick.util.LogSystem;

public final class Log {
    private static boolean verbose = true;
    private static boolean forcedVerbose = false;
    private static final String forceVerboseProperty = "org.newdawn.slick.forceVerboseLog";
    private static final String forceVerbosePropertyOnValue = "true";
    private static LogSystem logSystem = new DefaultLogSystem();

    private Log() {
    }

    public static void setLogSystem(LogSystem system) {
        logSystem = system;
    }

    public static void setVerbose(boolean v) {
        if (forcedVerbose) {
            return;
        }
        verbose = v;
    }

    public static void checkVerboseLogSetting() {
        try {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    String val = System.getProperty(Log.forceVerboseProperty);
                    if (val != null && val.equalsIgnoreCase(Log.forceVerbosePropertyOnValue)) {
                        Log.setForcedVerboseOn();
                    }
                    return null;
                }
            });
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void setForcedVerboseOn() {
        forcedVerbose = true;
        verbose = true;
    }

    public static void error(String message, Throwable e) {
        logSystem.error(message, e);
    }

    public static void error(Throwable e) {
        logSystem.error(e);
    }

    public static void error(String message) {
        logSystem.error(message);
    }

    public static void warn(String message) {
        logSystem.warn(message);
    }

    public static void warn(String message, Throwable e) {
        logSystem.warn(message, e);
    }

    public static void info(String message) {
        if (verbose || forcedVerbose) {
            logSystem.info(message);
        }
    }

    public static void debug(String message) {
        if (verbose || forcedVerbose) {
            logSystem.debug(message);
        }
    }
}

