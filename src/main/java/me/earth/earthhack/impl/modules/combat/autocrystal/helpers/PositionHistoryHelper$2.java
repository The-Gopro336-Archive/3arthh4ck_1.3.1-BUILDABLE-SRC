package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class PositionHistoryHelper$2
extends EventListener<WorldClientEvent.Load> {
    PositionHistoryHelper$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        PositionHistoryHelper.this.packets.clear();
    }
}
