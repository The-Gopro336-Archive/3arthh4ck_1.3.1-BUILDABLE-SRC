/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public class BBUtil {
    public static boolean intersects(AxisAlignedBB bb, Vec3i vec3i) {
        return bb.minX < (double)(vec3i.getX() + 1) && bb.maxX > (double)vec3i.getX() && bb.minY < (double)(vec3i.getY() + 1) && bb.maxY > (double)vec3i.getY() && bb.minZ < (double)(vec3i.getZ() + 1) && bb.maxZ > (double)vec3i.getZ();
    }
}

