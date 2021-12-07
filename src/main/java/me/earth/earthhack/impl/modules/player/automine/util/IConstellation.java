/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IConstellation {
    default public void update(IAutomine automine) {
    }

    public boolean isAffected(BlockPos var1, IBlockState var2);

    public boolean isValid(IBlockAccess var1, boolean var2);

    default public boolean cantBeImproved() {
        return true;
    }
}

