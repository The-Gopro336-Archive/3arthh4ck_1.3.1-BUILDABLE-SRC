package me.earth.earthhack.impl.util.math.geocache;

import net.minecraft.util.math.Vec3i;

public interface GeoCache {
    public void cache();

    public int getRadius(double var1);

    public Vec3i get(int var1);

    public Vec3i[] array();
}
