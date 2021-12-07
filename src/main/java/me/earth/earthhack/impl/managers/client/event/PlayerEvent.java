/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.client.event;

import java.util.UUID;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;

public class PlayerEvent
extends Event {
    private final PlayerEventType type;
    private final String name;
    private final UUID uuid;

    public PlayerEvent(PlayerEventType type, String name, UUID uuid) {
        this.type = type;
        this.name = name;
        this.uuid = uuid;
    }

    public PlayerEventType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}

