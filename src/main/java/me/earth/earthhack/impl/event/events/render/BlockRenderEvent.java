/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.event.events.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BlockRenderEvent {
    private final BlockPos pos;
    private final IBlockState state;

    public BlockRenderEvent(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public IBlockState getState() {
        return this.state;
    }
}

