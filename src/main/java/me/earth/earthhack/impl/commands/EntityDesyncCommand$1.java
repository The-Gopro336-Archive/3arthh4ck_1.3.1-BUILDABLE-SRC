package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class EntityDesyncCommand$1
extends EventListener<WorldClientEvent.Load> {
    EntityDesyncCommand$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        EntityDesyncCommand.this.dismounted = null;
    }
}
