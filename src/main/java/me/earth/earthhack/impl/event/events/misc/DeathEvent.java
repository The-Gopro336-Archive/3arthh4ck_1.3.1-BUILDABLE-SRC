/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 */
package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.EntityLivingBase;

public class DeathEvent {
    private final EntityLivingBase entity;

    public DeathEvent(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }
}

