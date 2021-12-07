package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEvent$Send<T extends Packet<? extends INetHandler>>
extends PacketEvent<T> {
    public PacketEvent$Send(T packet) {
        super((Packet)packet, null);
    }
}
