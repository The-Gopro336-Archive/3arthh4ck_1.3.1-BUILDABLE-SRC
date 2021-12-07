package org.newdawn.slick.particles;

import org.newdawn.slick.particles.ConfigurableEmitter;

public class ConfigurableEmitter$SimpleValue
implements ConfigurableEmitter.Value {
    private float value;
    private float next;

    private ConfigurableEmitter$SimpleValue(float value) {
        this.value = value;
    }

    @Override
    public float getValue(float time) {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
