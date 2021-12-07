package org.newdawn.slick.particles;

import java.security.PrivilegedAction;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

class ParticleSystem$1
implements PrivilegedAction {
    ParticleSystem$1() {
    }

    public Object run() {
        try {
            if (ParticleSystem.this.mask != null) {
                ParticleSystem.this.sprite = new Image(ParticleSystem.this.defaultImageName, ParticleSystem.this.mask);
            } else {
                ParticleSystem.this.sprite = new Image(ParticleSystem.this.defaultImageName);
            }
        }
        catch (SlickException e) {
            Log.error(e);
            ParticleSystem.this.defaultImageName = null;
        }
        return null;
    }
}
