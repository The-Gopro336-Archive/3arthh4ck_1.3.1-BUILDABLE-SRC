/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.particles;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

public interface ParticleEmitter {
    public void update(ParticleSystem var1, int var2);

    public boolean completed();

    public void wrapUp();

    public void updateParticle(Particle var1, int var2);

    public boolean isEnabled();

    public void setEnabled(boolean var1);

    public boolean useAdditive();

    public Image getImage();

    public boolean isOriented();

    public boolean usePoints(ParticleSystem var1);

    public void resetState();
}

