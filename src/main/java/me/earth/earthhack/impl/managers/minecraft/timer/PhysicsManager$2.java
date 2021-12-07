package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

class PhysicsManager$2
extends EventListener<DisconnectEvent> {
    PhysicsManager$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        PhysicsManager.this.times = 0;
    }
}
