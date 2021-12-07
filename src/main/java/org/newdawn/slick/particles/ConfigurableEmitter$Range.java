package org.newdawn.slick.particles;

public class ConfigurableEmitter$Range {
    private float max;
    private float min;
    private boolean enabled = false;

    private ConfigurableEmitter$Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float random() {
        return (float)((double)this.min + Math.random() * (double)(this.max - this.min));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getMax() {
        return this.max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return this.min;
    }

    public void setMin(float min) {
        this.min = min;
    }
}
