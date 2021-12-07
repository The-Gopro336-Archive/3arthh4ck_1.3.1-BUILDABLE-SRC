package me.earth.earthhack.impl.modules.render.crosshair;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.render.CrosshairEvent;

class Crosshair$1
extends EventListener<CrosshairEvent> {
    Crosshair$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(CrosshairEvent event) {
        event.setCancelled(true);
    }
}
