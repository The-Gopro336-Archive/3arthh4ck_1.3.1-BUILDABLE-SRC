package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class PositionHelper$1
extends EventListener<WorldClientEvent> {
    PositionHelper$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent event) {
        PositionHelper.this.motionTrackerMap.clear();
    }
}
