package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;

class Debug$3
extends EventListener<UpdateEntitiesEvent> {
    Debug$3(Class target) {
        super(target);
    }

    @Override
    public void invoke(UpdateEntitiesEvent event) {
    }
}
