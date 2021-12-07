package org.newdawn.slick.particles;

import java.util.ArrayList;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;

public class ConfigurableEmitter$LinearInterpolator
implements ConfigurableEmitter.Value {
    private ArrayList curve;
    private boolean active;
    private int min;
    private int max;

    public ConfigurableEmitter$LinearInterpolator(ArrayList curve, int min, int max) {
        this.curve = curve;
        this.min = min;
        this.max = max;
        this.active = false;
    }

    public void setCurve(ArrayList curve) {
        this.curve = curve;
    }

    public ArrayList getCurve() {
        return this.curve;
    }

    @Override
    public float getValue(float t) {
        Vector2f p0 = (Vector2f)this.curve.get(0);
        for (int i = 1; i < this.curve.size(); ++i) {
            Vector2f p1 = (Vector2f)this.curve.get(i);
            if (t >= p0.getX() && t <= p1.getX()) {
                float st = (t - p0.getX()) / (p1.getX() - p0.getX());
                float r = p0.getY() + st * (p1.getY() - p0.getY());
                return r;
            }
            p0 = p1;
        }
        return 0.0f;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
