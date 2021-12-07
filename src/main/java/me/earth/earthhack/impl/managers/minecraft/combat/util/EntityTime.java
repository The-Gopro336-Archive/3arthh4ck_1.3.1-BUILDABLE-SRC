/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.managers.minecraft.combat.util;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.entity.Entity;

public class EntityTime {
    private final AtomicBoolean valid = new AtomicBoolean(true);
    private final Entity entity;
    public long time;

    public EntityTime(Entity entity) {
        this.entity = entity;
        this.time = System.currentTimeMillis();
    }

    public boolean passed(long ms) {
        return ms <= 0L || System.currentTimeMillis() - this.time > ms;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }

    public boolean isValid() {
        return this.valid.get();
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }
}

