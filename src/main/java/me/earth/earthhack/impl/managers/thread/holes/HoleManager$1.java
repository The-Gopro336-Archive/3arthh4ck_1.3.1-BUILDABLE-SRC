package me.earth.earthhack.impl.managers.thread.holes;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class HoleManager$1
extends EventListener<TickEvent> {
    HoleManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        HoleManager.this.runTick();
    }
}
