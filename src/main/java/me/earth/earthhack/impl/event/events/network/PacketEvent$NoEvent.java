package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEvent$NoEvent<T extends Packet<? extends INetHandler>>
extends PacketEvent<T> {
    private final boolean post;

    public PacketEvent$NoEvent(T packet, boolean post) {
        super((Packet)packet, null);
        this.post = post;
    }

    public boolean hasPost() {
        return this.post;
    }
}
