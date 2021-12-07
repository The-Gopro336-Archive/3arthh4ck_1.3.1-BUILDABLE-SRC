/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.server.SPacketHeldItemChange
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;

final class ListenerHeldItemChange
extends ModuleListener<Packets, PacketEvent.Receive<SPacketHeldItemChange>> {
    public ListenerHeldItemChange(Packets module) {
        super(module, PacketEvent.Receive.class, SPacketHeldItemChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketHeldItemChange> event) {
        if (((Packets)this.module).noHandChange.getValue().booleanValue() && ListenerHeldItemChange.mc.player != null) {
            event.setCancelled(true);
            mc.addScheduledTask(Locks.wrap(Locks.PLACE_SWITCH_LOCK, () -> {
                if (ListenerHeldItemChange.mc.player == null || mc.getConnection() == null) {
                    return;
                }
                int l = ListenerHeldItemChange.mc.player.inventory.currentItem;
                if (l != ((SPacketHeldItemChange)event.getPacket()).getHeldItemHotbarIndex()) {
                    ListenerHeldItemChange.mc.player.inventory.currentItem = l;
                    mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(l));
                }
            }));
        }
    }
}

