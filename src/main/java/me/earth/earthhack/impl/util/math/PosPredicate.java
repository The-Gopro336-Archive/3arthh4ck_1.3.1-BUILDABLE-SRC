/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface PosPredicate {
    public boolean test(BlockPos var1);
}

