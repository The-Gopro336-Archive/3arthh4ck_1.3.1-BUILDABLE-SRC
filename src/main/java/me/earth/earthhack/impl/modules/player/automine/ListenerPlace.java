/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 */
package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPlace
extends ModuleListener<AutoMine, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerPlace(AutoMine module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        if (ListenerPlace.mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) {
            ((AutoMine)this.module).downTimer.reset();
        }
    }
}

