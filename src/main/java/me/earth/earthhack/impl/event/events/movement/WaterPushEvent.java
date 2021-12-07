/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.Entity;

public class WaterPushEvent
extends Event {
    private final Entity entity;

    public WaterPushEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

