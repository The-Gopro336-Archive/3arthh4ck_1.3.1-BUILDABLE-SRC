/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.FriendSerializer;

final class ListenerTick
extends EventListener<TickEvent> {
    private final FriendSerializer serializer;

    public ListenerTick(FriendSerializer serializer) {
        super(TickEvent.class);
        this.serializer = serializer;
    }

    @Override
    public void invoke(TickEvent event) {
        this.serializer.onTick();
    }
}

