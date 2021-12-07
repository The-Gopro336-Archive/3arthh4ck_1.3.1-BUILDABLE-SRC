/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.util.BlockRenderLayer
 */
package me.earth.earthhack.impl.event.events.render;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

public class BlockLayerEvent {
    private BlockRenderLayer layer = null;
    private final Block block;

    public BlockLayerEvent(Block block) {
        this.block = block;
    }

    public void setLayer(BlockRenderLayer layer) {
        this.layer = layer;
    }

    public BlockRenderLayer getLayer() {
        return this.layer;
    }

    public Block getBlock() {
        return this.block;
    }
}

