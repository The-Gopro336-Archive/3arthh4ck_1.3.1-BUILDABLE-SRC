package me.earth.earthhack.impl.managers.render;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class ColorManager$1
extends EventListener<TickEvent> {
    ColorManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        ColorManager.this.update();
    }
}
