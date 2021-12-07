package org.newdawn.slick.particles;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitterFactory;

final class ParticleIO$1
implements ConfigurableEmitterFactory {
    ParticleIO$1() {
    }

    @Override
    public ConfigurableEmitter createEmitter(String name) {
        return new ConfigurableEmitter(name);
    }
}
