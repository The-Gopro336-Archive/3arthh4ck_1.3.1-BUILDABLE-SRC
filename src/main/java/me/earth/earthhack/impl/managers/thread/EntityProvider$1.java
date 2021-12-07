package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class EntityProvider$1
extends EventListener<TickEvent.PostWorldTick> {
    EntityProvider$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent.PostWorldTick event) {
        EntityProvider.this.update();
    }
}
