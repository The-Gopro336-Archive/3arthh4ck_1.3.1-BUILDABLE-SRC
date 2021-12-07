/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.INetHandler
 *  net.minecraft.network.Packet
 */
package me.earth.earthhack.impl.event.events.network;

import java.util.ArrayDeque;
import java.util.Deque;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketEvent<T extends Packet<? extends INetHandler>>
extends Event {
    private final T packet;

    private PacketEvent(T packet) {
        this.packet = packet;
    }

    public T getPacket() {
        return this.packet;
    }

    /* synthetic */ PacketEvent(Packet x0, 1 x1) {
        this(x0);
    }

    public static class Post<T extends Packet<? extends INetHandler>>
    extends PacketEvent<T> {
        public Post(T packet) {
            super((Packet)packet, null);
        }
    }

    public static class Receive<T extends Packet<? extends INetHandler>>
    extends PacketEvent<T> {
        private final Deque<Runnable> postEvents = new ArrayDeque<Runnable>();

        public Receive(T packet) {
            super((Packet)packet, null);
        }

        public void addPostEvent(SafeRunnable runnable) {
            this.postEvents.add(runnable);
        }

        public Deque<Runnable> getPostEvents() {
            return this.postEvents;
        }
    }

    public static class NoEvent<T extends Packet<? extends INetHandler>>
    extends PacketEvent<T> {
        private final boolean post;

        public NoEvent(T packet, boolean post) {
            super((Packet)packet, null);
            this.post = post;
        }

        public boolean hasPost() {
            return this.post;
        }
    }

    public static class Send<T extends Packet<? extends INetHandler>>
    extends PacketEvent<T> {
        public Send(T packet) {
            super((Packet)packet, null);
        }
    }
}

