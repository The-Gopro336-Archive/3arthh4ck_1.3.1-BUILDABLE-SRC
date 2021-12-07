/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerTryUseItem
extends ModuleListener<FastEat, PacketEvent.Send<CPacketPlayerTryUseItem>> {
    public ListenerTryUseItem(FastEat module) {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event) {
        if (((FastEat)this.module).mode.getValue() == FastEatMode.Update && ((FastEat)this.module).isValid(ListenerTryUseItem.mc.player.getHeldItem(((CPacketPlayerTryUseItem)event.getPacket()).getHand()))) {
            NetworkUtil.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
}

