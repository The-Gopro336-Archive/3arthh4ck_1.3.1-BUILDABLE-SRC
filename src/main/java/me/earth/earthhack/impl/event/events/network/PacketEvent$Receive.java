package me.earth.earthhack.impl.event.events.network;

import java.util.ArrayDeque;
import java.util.Deque;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEvent$Receive<T extends Packet<? extends INetHandler>>
extends PacketEvent<T> {
    private final Deque<Runnable> postEvents = new ArrayDeque<Runnable>();

    public PacketEvent$Receive(T packet) {
        super((Packet)packet, null);
    }

    public void addPostEvent(SafeRunnable runnable) {
        this.postEvents.add(runnable);
    }

    public Deque<Runnable> getPostEvents() {
        return this.postEvents;
    }
}
