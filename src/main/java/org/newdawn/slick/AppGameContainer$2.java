package org.newdawn.slick;

import java.security.PrivilegedAction;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.util.Log;

class AppGameContainer$2
implements PrivilegedAction {
    AppGameContainer$2() {
    }

    public Object run() {
        try {
            PixelFormat format = new PixelFormat(8, 8, GameContainer.stencil ? 8 : 0, AppGameContainer.this.samples);
            AppGameContainer.this.tryCreateDisplay(format);
            AppGameContainer.this.supportsMultiSample = true;
        }
        catch (Exception e) {
            Display.destroy();
            try {
                PixelFormat format = new PixelFormat(8, 8, GameContainer.stencil ? 8 : 0);
                AppGameContainer.this.tryCreateDisplay(format);
                AppGameContainer.this.alphaSupport = false;
            }
            catch (Exception e2) {
                Display.destroy();
                try {
                    AppGameContainer.this.tryCreateDisplay(new PixelFormat());
                }
                catch (Exception e3) {
                    Log.error(e3);
                }
            }
        }
        return null;
    }
}
