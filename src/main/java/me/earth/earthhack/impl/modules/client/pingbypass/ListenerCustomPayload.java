/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 *  net.minecraft.network.play.server.SPacketCustomPayload
 */
package me.earth.earthhack.impl.modules.client.pingbypass;

import io.netty.util.ReferenceCounted;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadManager;
import net.minecraft.network.play.server.SPacketCustomPayload;

final class ListenerCustomPayload
extends EventListener<PacketEvent.Receive<SPacketCustomPayload>> {
    private final PayloadManager manager;

    public ListenerCustomPayload(PayloadManager manager) {
        super(PacketEvent.Receive.class, SPacketCustomPayload.class);
        this.manager = manager;
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event) {
        if (((SPacketCustomPayload)event.getPacket()).getChannelName().equalsIgnoreCase("PingBypass")) {
            try {
                this.manager.onPacket((SPacketCustomPayload)event.getPacket());
                event.setCancelled(true);
            }
            finally {
                BufferUtil.releaseBuffer((ReferenceCounted)((SPacketCustomPayload)event.getPacket()).getBufferData());
            }
        }
    }
}

