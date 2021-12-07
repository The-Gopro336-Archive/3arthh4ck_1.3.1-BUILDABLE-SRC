package org.newdawn.slick.particles.effects;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

public class FireEmitter
implements ParticleEmitter {
    private int x;
    private int y;
    private int interval = 50;
    private int timer;
    private float size = 40.0f;

    public FireEmitter() {
    }

    public FireEmitter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public FireEmitter(int x, int y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    public void update(ParticleSystem system, int delta) {
        this.timer -= delta;
        if (this.timer <= 0) {
            this.timer = this.interval;
            Particle p = system.getNewParticle(this, 1000.0f);
            p.setColor(1.0f, 1.0f, 1.0f, 0.5f);
            p.setPosition(this.x, this.y);
            p.setSize(this.size);
            float vx = (float)((double)-0.02f + Math.random() * (double)0.04f);
            float vy = (float)(-(Math.random() * (double)0.15f));
            p.setVelocity(vx, vy, 1.1f);
        }
    }

    @Override
    public void updateParticle(Particle particle, int delta) {
        if (particle.getLife() > 600.0f) {
            particle.adjustSize(0.07f * (float)delta);
        } else {
            particle.adjustSize(-0.04f * (float)delta * (this.size / 40.0f));
        }
        float c = 0.002f * (float)delta;
        particle.adjustColor(0.0f, -c / 2.0f, -c * 2.0f, -c / 4.0f);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean completed() {
        return false;
    }

    @Override
    public boolean useAdditive() {
        return false;
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public boolean usePoints(ParticleSystem system) {
        return false;
    }

    @Override
    public boolean isOriented() {
        return false;
    }

    @Override
    public void wrapUp() {
    }

    @Override
    public void resetState() {
    }
}
