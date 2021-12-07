/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 */
package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerUseItem
extends ModuleListener<Tracker, PacketEvent.Post<CPacketPlayerTryUseItem>> {
    public ListenerUseItem(Tracker module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItem> event) {
        CPacketPlayerTryUseItem p = (CPacketPlayerTryUseItem)event.getPacket();
        if (ListenerUseItem.mc.player.getHeldItem(p.getHand()).getItem() == Items.EXPERIENCE_BOTTLE) {
            ((Tracker)this.module).awaitingExp.incrementAndGet();
        }
    }
}

