package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class SetDeadManager$5
extends EventListener<WorldClientEvent.Unload> {
    SetDeadManager$5(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Unload event) {
        SetDeadManager.this.clear();
    }
}
