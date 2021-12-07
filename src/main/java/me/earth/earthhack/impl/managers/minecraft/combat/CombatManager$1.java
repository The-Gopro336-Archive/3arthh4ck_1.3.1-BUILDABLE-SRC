package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;

class CombatManager$1
extends EventListener<DeathEvent> {
    CombatManager$1(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(DeathEvent event) {
        CombatManager.this.onDeath(event.getEntity());
    }
}
