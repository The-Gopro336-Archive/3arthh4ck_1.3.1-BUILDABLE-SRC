/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.antisurround.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface AntiSurroundFunction {
    public void accept(BlockPos var1, BlockPos var2, BlockPos var3, EnumFacing var4, int var5, MineSlots var6, int var7, Entity var8, EntityPlayer var9, boolean var10);
}

