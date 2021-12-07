package org.newdawn.slick.particles;

import org.newdawn.slick.particles.ConfigurableEmitter;

public class ConfigurableEmitter$RandomValue
implements ConfigurableEmitter.Value {
    private float value;

    private ConfigurableEmitter$RandomValue(float value) {
        this.value = value;
    }

    @Override
    public float getValue(float time) {
        return (float)(Math.random() * (double)this.value);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }
}
