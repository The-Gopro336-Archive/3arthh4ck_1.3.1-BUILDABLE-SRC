/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.entityspeed.EntitySpeed;

final class EntitySpeedData
extends DefaultData<EntitySpeed> {
    public EntitySpeedData(EntitySpeed module) {
        super(module);
        this.register(module.speed, "The speed to move with.");
        this.register(module.noStuck, "Prevents you from getting stuck while riding.");
        this.register(module.resetStuck, "Makes you slower when using NoStuck.");
        this.register(module.stuckTime, "Time to be slowed down for after getting stuck.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Speed up the entity you are riding on.";
    }
}

