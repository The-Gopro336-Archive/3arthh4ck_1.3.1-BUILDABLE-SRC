package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class Debug$1
extends EventListener<TickEvent> {
    Debug$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
    }
}
