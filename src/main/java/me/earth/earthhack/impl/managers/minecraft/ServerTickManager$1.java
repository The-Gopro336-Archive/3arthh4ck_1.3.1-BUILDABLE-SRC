package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class ServerTickManager$1
extends EventListener<WorldClientEvent> {
    ServerTickManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent event) {
        if (event.getClient().isRemote) {
            ServerTickManager.this.reset();
        }
    }
}
