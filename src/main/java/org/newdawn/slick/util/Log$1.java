package org.newdawn.slick.util;

import java.security.PrivilegedAction;
import org.newdawn.slick.util.Log;

final class Log$1
implements PrivilegedAction {
    Log$1() {
    }

    public Object run() {
        String val = System.getProperty(Log.forceVerboseProperty);
        if (val != null && val.equalsIgnoreCase(Log.forceVerbosePropertyOnValue)) {
            Log.setForcedVerboseOn();
        }
        return null;
    }
}
