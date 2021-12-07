/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.INetHandlerPlayClient
 *  net.minecraft.network.play.server.SPacketPlayerListHeaderFooter
 */
package me.earth.earthhack.impl.modules.misc.packets;

import java.util.Objects;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;

final class ListenerPlayerListHeader
extends ModuleListener<Packets, PacketEvent.Receive<SPacketPlayerListHeaderFooter>> {
    public ListenerPlayerListHeader(Packets module) {
        super(module, PacketEvent.Receive.class, -2147483647, SPacketPlayerListHeaderFooter.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerListHeaderFooter> event) {
        SPacketPlayerListHeaderFooter packet;
        if (((Packets)this.module).safeHeaders.getValue().booleanValue() && ((packet = (SPacketPlayerListHeaderFooter)event.getPacket()).getHeader().getFormattedText().isEmpty() || packet.getFooter().getFormattedText().isEmpty())) {
            event.setCancelled(true);
            mc.addScheduledTask(() -> packet.processPacket((INetHandlerPlayClient)Objects.requireNonNull(mc.getConnection())));
        }
    }
}

