/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.FriendSerializer;

final class ListenerDisconnect
extends EventListener<DisconnectEvent> {
    private final FriendSerializer serializer;

    public ListenerDisconnect(FriendSerializer serializer) {
        super(DisconnectEvent.class);
        this.serializer = serializer;
    }

    @Override
    public void invoke(DisconnectEvent event) {
        this.serializer.clear();
    }
}

