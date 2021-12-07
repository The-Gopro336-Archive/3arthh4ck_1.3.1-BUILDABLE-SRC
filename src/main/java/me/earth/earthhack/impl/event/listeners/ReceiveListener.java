package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.api.Invoker;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.network.Packet;

public class ReceiveListener<P extends Packet<?>>
extends LambdaListener<PacketEvent.Receive<P>> {
    public ReceiveListener(Class<P> target, Invoker<PacketEvent.Receive<P>> invoker) {
        this(target, 10, invoker);
    }

    public ReceiveListener(Class<P> target, int priority, Invoker<PacketEvent.Receive<P>> invoker) {
        super(PacketEvent.Receive.class, priority, target, invoker);
    }
}
