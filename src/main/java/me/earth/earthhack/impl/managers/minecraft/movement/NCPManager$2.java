package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class NCPManager$2
extends EventListener<WorldClientEvent.Load> {
    NCPManager$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        NCPManager.this.endedSneak = false;
        NCPManager.this.endedSprint = false;
        NCPManager.this.windowClicks = false;
    }
}
