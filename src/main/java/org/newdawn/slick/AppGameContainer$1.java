package org.newdawn.slick;

import java.security.PrivilegedAction;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

final class AppGameContainer$1
implements PrivilegedAction {
    AppGameContainer$1() {
    }

    public Object run() {
        try {
            Display.getDisplayMode();
        }
        catch (Exception e) {
            Log.error(e);
        }
        return null;
    }
}
