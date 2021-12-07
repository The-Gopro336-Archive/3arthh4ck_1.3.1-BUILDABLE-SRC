package me.earth.earthhack.impl.modules.render.sounds.util;

import java.util.Objects;

public class SoundPosition {
    private final double x;
    private final double y;
    private final double z;
    private final String name;

    public SoundPosition(double x, double y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = Objects.requireNonNull(name);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + (int)this.x;
        hash = hash * 31 + (int)this.y;
        hash = hash * 31 + (int)this.z;
        hash = hash * 31 + this.name.hashCode();
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SoundPosition) {
            SoundPosition o = (SoundPosition)obj;
            return this.x == o.x && this.y == o.y && this.z == o.z && this.name.equals(o.name);
        }
        return false;
    }
}
