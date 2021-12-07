package org.newdawn.slick;

import java.security.PrivilegedAction;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

class Graphics$1
implements PrivilegedAction {
    Graphics$1() {
    }

    public Object run() {
        try {
            DEFAULT_FONT = new AngelCodeFont("org/newdawn/slick/data/defaultfont.fnt", "org/newdawn/slick/data/defaultfont.png");
        }
        catch (SlickException e) {
            Log.error(e);
        }
        return null;
    }
}
