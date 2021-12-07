package org.newdawn.slick.openal;

import java.security.PrivilegedAction;
import org.lwjgl.openal.AL;
import org.newdawn.slick.util.Log;

class SoundStore$1
implements PrivilegedAction {
    SoundStore$1() {
    }

    public Object run() {
        try {
            AL.create();
            SoundStore.this.soundWorks = true;
            SoundStore.this.sounds = true;
            SoundStore.this.music = true;
            Log.info("- Sound works");
        }
        catch (Exception e) {
            Log.error("Sound initialisation failure.");
            Log.error(e);
            SoundStore.this.soundWorks = false;
            SoundStore.this.sounds = false;
            SoundStore.this.music = false;
        }
        return null;
    }
}
