/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.AxisAlignedBB;

public class MutableBB
extends AxisAlignedBB {
    protected double minX;
    protected double minY;
    protected double minZ;
    protected double maxX;
    protected double maxY;
    protected double maxZ;

    public MutableBB() {
        this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public MutableBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
}

