/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.BlockRenderLayer
 */
package me.earth.earthhack.forge.mixins.block;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.BlockLayerEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Block.class})
public abstract class MixinBlock {
    @Inject(method={"canRenderInLayer"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    @Dynamic
    private void canRenderInLayerHook(IBlockState state, BlockRenderLayer layer, CallbackInfoReturnable<Boolean> info) {
        Block block = (Block)Block.class.cast(this);
        BlockLayerEvent event = new BlockLayerEvent(block);
        Bus.EVENT_BUS.post(event);
        if (event.getLayer() != null) {
            info.setReturnValue(event.getLayer() == layer);
        }
    }
}

