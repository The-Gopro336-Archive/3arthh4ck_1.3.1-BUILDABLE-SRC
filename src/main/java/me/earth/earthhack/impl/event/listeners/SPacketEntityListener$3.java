package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntity;

class SPacketEntityListener$3
extends EventListener<PacketEvent.Receive<SPacketEntity.S16PacketEntityLook>> {
    SPacketEntityListener$3(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) {
        SPacketEntityListener.this.onRotation(event);
    }
}
