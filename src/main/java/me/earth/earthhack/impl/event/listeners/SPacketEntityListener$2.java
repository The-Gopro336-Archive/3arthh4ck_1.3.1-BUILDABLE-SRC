package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntity;

class SPacketEntityListener$2
extends EventListener<PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove>> {
    SPacketEntityListener$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
        SPacketEntityListener.this.onPosition(event);
    }
}
