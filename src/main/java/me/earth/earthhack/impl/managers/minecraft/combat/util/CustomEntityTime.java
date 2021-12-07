/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.managers.minecraft.combat.util;

import me.earth.earthhack.impl.managers.minecraft.combat.util.EntityTime;
import net.minecraft.entity.Entity;

public class CustomEntityTime
extends EntityTime {
    private final long customTime;

    public CustomEntityTime(Entity entity, long customTime) {
        super(entity);
        this.customTime = customTime;
    }

    @Override
    public boolean passed(long ms) {
        return System.currentTimeMillis() - this.time > this.customTime;
    }
}

