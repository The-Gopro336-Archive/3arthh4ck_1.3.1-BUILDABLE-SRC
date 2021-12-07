/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemFood
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

final class ListenerUseOnBlock
extends ModuleListener<FastPlace, PacketEvent.Send<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerUseOnBlock(FastPlace module) {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItemOnBlock> event) {
        if (((FastPlace)this.module).bypass.getValue().booleanValue() && ListenerUseOnBlock.mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() == Items.EXPERIENCE_BOTTLE || ((FastPlace)this.module).foodBypass.getValue().booleanValue() && ListenerUseOnBlock.mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() instanceof ItemFood) {
            if (Managers.ACTION.isSneaking() || ((FastPlace)this.module).bypassContainers.getValue().booleanValue()) {
                event.setCancelled(true);
            } else {
                BlockPos pos = ((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getPos();
                IBlockState state = ListenerUseOnBlock.mc.world.getBlockState(pos);
                if (!SpecialBlocks.BAD_BLOCKS.contains((Object)state.getBlock()) && !SpecialBlocks.SHULKERS.contains((Object)state.getBlock())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

