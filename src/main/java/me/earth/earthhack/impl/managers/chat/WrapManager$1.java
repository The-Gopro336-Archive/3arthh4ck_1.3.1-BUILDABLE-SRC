package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class WrapManager$1
extends EventListener<TickEvent> {
    WrapManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        WrapManager.this.onTick();
    }
}
