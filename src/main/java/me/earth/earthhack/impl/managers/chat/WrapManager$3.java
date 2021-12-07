package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class WrapManager$3
extends EventListener<WorldClientEvent.Load> {
    WrapManager$3(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        Globals.mc.addScheduledTask(() -> WrapManager.this.clear());
    }
}
