/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 */
package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class LiquidJumpEvent
extends Event {
    private final EntityLivingBase entity;

    public LiquidJumpEvent(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }
}

