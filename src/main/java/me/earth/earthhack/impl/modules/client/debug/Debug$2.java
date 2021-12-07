package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;

class Debug$2
extends EventListener<MotionUpdateEvent> {
    Debug$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
    }
}
