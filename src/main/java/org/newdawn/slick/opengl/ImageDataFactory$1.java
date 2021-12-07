package org.newdawn.slick.opengl;

import java.security.PrivilegedAction;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.util.Log;

final class ImageDataFactory$1
implements PrivilegedAction {
    ImageDataFactory$1() {
    }

    public Object run() {
        String val = System.getProperty(ImageDataFactory.PNG_LOADER);
        if ("false".equalsIgnoreCase(val)) {
            usePngLoader = false;
        }
        Log.info("Use Java PNG Loader = " + usePngLoader);
        return null;
    }
}
