package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class SetDeadManager$4
extends EventListener<WorldClientEvent.Load> {
    SetDeadManager$4(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        SetDeadManager.this.clear();
    }
}
