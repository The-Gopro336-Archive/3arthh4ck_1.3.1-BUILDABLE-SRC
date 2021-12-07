package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

class CPacketPlayerPostListener$1
extends EventListener<PacketEvent.Post<CPacketPlayer>> {
    CPacketPlayerPostListener$1(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayer> event) {
        CPacketPlayerPostListener.this.onPacket(event);
    }
}
