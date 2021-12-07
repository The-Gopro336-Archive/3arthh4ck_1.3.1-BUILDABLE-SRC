package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;

class PingSpoof$1
extends EventListener<ShutDownEvent> {
    PingSpoof$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(ShutDownEvent event) {
        PingSpoof.this.service.shutdown();
    }
}
