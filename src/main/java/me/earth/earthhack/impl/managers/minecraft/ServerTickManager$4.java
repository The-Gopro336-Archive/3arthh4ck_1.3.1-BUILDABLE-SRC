package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

class ServerTickManager$4
extends EventListener<DisconnectEvent> {
    ServerTickManager$4(Class target) {
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        ServerTickManager.this.initialized = false;
    }
}
