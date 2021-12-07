package org.newdawn.slick.particles;

import java.util.ArrayList;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

class ParticleSystem$ParticlePool {
    public Particle[] particles;
    public ArrayList available;

    public ParticleSystem$ParticlePool(ParticleSystem system, int maxParticles) {
        this.particles = new Particle[maxParticles];
        this.available = new ArrayList();
        for (int i = 0; i < this.particles.length; ++i) {
            this.particles[i] = ParticleSystem.this.createParticle(system);
        }
        this.reset(system);
    }

    public void reset(ParticleSystem system) {
        this.available.clear();
        for (int i = 0; i < this.particles.length; ++i) {
            this.available.add(this.particles[i]);
        }
    }
}
