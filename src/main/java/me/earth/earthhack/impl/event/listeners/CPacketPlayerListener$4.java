package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

class CPacketPlayerListener$4
extends EventListener<PacketEvent.Send<CPacketPlayer.PositionRotation>> {
    CPacketPlayerListener$4(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        CPacketPlayerListener.this.onPositionRotation(event);
    }
}
