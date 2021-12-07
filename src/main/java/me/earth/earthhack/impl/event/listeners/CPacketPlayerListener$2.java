package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

class CPacketPlayerListener$2
extends EventListener<PacketEvent.Send<CPacketPlayer.Position>> {
    CPacketPlayerListener$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayer.Position> event) {
        CPacketPlayerListener.this.onPosition(event);
    }
}
