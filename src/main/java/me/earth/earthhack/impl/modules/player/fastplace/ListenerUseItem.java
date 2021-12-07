/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemFood
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerUseItem
extends ModuleListener<FastPlace, PacketEvent.Send<CPacketPlayerTryUseItem>> {
    private boolean sending;

    public ListenerUseItem(FastPlace module) {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event) {
        if (!((FastPlace)this.module).doubleEat.getValue().booleanValue() || this.sending || event.isCancelled() || !(ListenerUseItem.mc.player.getHeldItem(((CPacketPlayerTryUseItem)event.getPacket()).getHand()).getItem() instanceof ItemFood)) {
            return;
        }
        this.sending = true;
        ListenerUseItem.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(((CPacketPlayerTryUseItem)event.getPacket()).getHand()));
        this.sending = false;
        ListenerUseItem.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }
}

