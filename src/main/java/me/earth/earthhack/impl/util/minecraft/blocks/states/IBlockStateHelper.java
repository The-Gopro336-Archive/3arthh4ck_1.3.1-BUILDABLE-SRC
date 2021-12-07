package me.earth.earthhack.impl.util.minecraft.blocks.states;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockStateHelper
extends IBlockAccess {
    default public void addAir(BlockPos pos) {
        this.addBlockState(pos, Blocks.AIR.getDefaultState());
    }

    public void addBlockState(BlockPos var1, IBlockState var2);

    public void delete(BlockPos var1);

    public void clearAllStates();
}
