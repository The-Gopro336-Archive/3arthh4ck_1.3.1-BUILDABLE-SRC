package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

class WrapManager$2
extends EventListener<DisconnectEvent> {
    WrapManager$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        Globals.mc.addScheduledTask(() -> WrapManager.this.clear());
    }
}
