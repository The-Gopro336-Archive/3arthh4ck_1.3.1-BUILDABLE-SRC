package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;

final class VelocityData
extends DefaultData<Velocity> {
    protected VelocityData(Velocity velocity) {
        super(velocity);
        this.descriptions.put(((Velocity)this.module).knockBack, "Block the knockback you take from Hits.");
        this.descriptions.put(((Velocity)this.module).horizontal, "The factor of the horizontal knockback you receive.");
        this.descriptions.put(((Velocity)this.module).vertical, "The factor of the vertical knockback you receive.");
        this.descriptions.put(((Velocity)this.module).noPush, "Prevent getting pushed by other entities.");
        this.descriptions.put(((Velocity)this.module).explosions, "Block knockback received from explosions.");
        this.descriptions.put(((Velocity)this.module).bobbers, "Block fishing rod bobbers from moving you.");
        this.descriptions.put(((Velocity)this.module).water, "Prevent water from pushing you.");
        this.descriptions.put(((Velocity)this.module).blocks, "Prevent Blocks from pushing you out e.g. if you phased into one.");
        this.descriptions.put(((Velocity)this.module).shulkers, "Prevents Shulkers from pushing you.");
    }

    @Override
    public int getColor() {
        return -13612801;
    }

    @Override
    public String getDescription() {
        return "Stops knockback from various sources.";
    }
}
