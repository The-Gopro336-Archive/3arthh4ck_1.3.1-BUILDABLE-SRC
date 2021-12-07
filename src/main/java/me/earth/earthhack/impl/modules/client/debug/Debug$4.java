package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class Debug$4
extends EventListener<WorldClientEvent> {
    Debug$4(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent event) {
        Debug.this.reset();
    }
}
