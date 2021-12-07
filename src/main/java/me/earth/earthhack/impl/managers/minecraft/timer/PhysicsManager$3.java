package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class PhysicsManager$3
extends EventListener<WorldClientEvent.Load> {
    PhysicsManager$3(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        PhysicsManager.this.times = 0;
    }
}
