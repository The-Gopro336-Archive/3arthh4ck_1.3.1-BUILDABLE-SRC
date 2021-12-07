/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.WorldType
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.Chunk
 */
package me.earth.earthhack.impl.util.minecraft.blocks.states;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BlockStateHelper
implements Globals,
IBlockStateHelper {
    private final Map<BlockPos, IBlockState> states;

    public BlockStateHelper() {
        this(new HashMap<BlockPos, IBlockState>());
    }

    public BlockStateHelper(Map<BlockPos, IBlockState> stateMap) {
        this.states = stateMap;
    }

    public IBlockState getBlockState(BlockPos pos) {
        IBlockState state = this.states.get((Object)pos);
        if (state == null) {
            return BlockStateHelper.mc.world.getBlockState(pos);
        }
        return state;
    }

    @Override
    public void addBlockState(BlockPos pos, IBlockState state) {
        this.states.putIfAbsent(pos.toImmutable(), state);
    }

    @Override
    public void delete(BlockPos pos) {
        this.states.remove((Object)pos);
    }

    @Override
    public void clearAllStates() {
        this.states.clear();
    }

    public TileEntity getTileEntity(BlockPos pos) {
        return BlockStateHelper.mc.world.getTileEntity(pos);
    }

    public int getCombinedLight(BlockPos pos, int lightValue) {
        return BlockStateHelper.mc.world.getCombinedLight(pos, lightValue);
    }

    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getBlock().isAir(this.getBlockState(pos), (IBlockAccess)this, pos);
    }

    public Biome getBiome(BlockPos pos) {
        return BlockStateHelper.mc.world.getBiome(pos);
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.getBlockState(pos).getStrongPower((IBlockAccess)this, pos, direction);
    }

    public WorldType getWorldType() {
        return BlockStateHelper.mc.world.getWorldType();
    }

    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        if (!BlockStateHelper.mc.world.isValid(pos)) {
            return _default;
        }
        Chunk chunk = BlockStateHelper.mc.world.getChunk(pos);
        if (chunk == null || chunk.isEmpty()) {
            return _default;
        }
        return this.getBlockState(pos).isSideSolid((IBlockAccess)this, pos, side);
    }
}

