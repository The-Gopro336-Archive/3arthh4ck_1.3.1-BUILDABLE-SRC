/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTransaction
 *  net.minecraft.network.play.server.SPacketConfirmTransaction
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

final class ListenerConfirmTransaction
extends ModuleListener<Packets, PacketEvent.Receive<SPacketConfirmTransaction>> {
    public ListenerConfirmTransaction(Packets module) {
        super(module, PacketEvent.Receive.class, -1000, SPacketConfirmTransaction.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketConfirmTransaction> event) {
        SPacketConfirmTransaction packet;
        if (!event.isCancelled() && ((Packets)this.module).fastTransactions.getValue().booleanValue() && ListenerConfirmTransaction.mc.player != null && !(packet = (SPacketConfirmTransaction)event.getPacket()).wasAccepted() && (packet.getWindowId() == 0 ? ListenerConfirmTransaction.mc.player.inventoryContainer : ListenerConfirmTransaction.mc.player.openContainer) != null) {
            event.setCancelled(true);
            ListenerConfirmTransaction.mc.player.connection.sendPacket((Packet)new CPacketConfirmTransaction(packet.getWindowId(), packet.getActionNumber(), true));
        }
    }
}

