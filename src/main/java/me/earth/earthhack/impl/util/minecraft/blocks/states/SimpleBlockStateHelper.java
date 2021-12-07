package me.earth.earthhack.impl.util.minecraft.blocks.states;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public enum SimpleBlockStateHelper implements Globals,
IBlockStateHelper
{
    INSTANCE;


    @Override
    public void addBlockState(BlockPos pos, IBlockState state) {
    }

    @Override
    public void delete(BlockPos pos) {
    }

    @Override
    public void clearAllStates() {
    }

    public TileEntity getTileEntity(BlockPos pos) {
        return SimpleBlockStateHelper.mc.world.getTileEntity(pos);
    }

    public int getCombinedLight(BlockPos pos, int lightValue) {
        return SimpleBlockStateHelper.mc.world.getCombinedLight(pos, lightValue);
    }

    public IBlockState getBlockState(BlockPos pos) {
        return SimpleBlockStateHelper.mc.world.getBlockState(pos);
    }

    public boolean isAirBlock(BlockPos pos) {
        return SimpleBlockStateHelper.mc.world.isAirBlock(pos);
    }

    public Biome getBiome(BlockPos pos) {
        return SimpleBlockStateHelper.mc.world.getBiome(pos);
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return SimpleBlockStateHelper.mc.world.getStrongPower(pos, direction);
    }

    public WorldType getWorldType() {
        return SimpleBlockStateHelper.mc.world.getWorldType();
    }

    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return SimpleBlockStateHelper.mc.world.isSideSolid(pos, side, _default);
    }
}
