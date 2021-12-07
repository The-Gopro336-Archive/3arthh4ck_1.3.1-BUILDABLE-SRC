package org.newdawn.slick.particles;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitterFactory;

final class ParticleIO$2
implements ConfigurableEmitterFactory {
    ParticleIO$2() {
    }

    @Override
    public ConfigurableEmitter createEmitter(String name) {
        return new ConfigurableEmitter(name);
    }
}
