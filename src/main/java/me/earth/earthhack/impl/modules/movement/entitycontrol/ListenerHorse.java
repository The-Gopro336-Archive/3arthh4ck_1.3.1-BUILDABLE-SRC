package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.event.events.movement.HorseEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.entitycontrol.EntityControl;

final class ListenerHorse
extends ModuleListener<EntityControl, HorseEvent> {
    public ListenerHorse(EntityControl module) {
        super(module, HorseEvent.class);
    }

    @Override
    public void invoke(HorseEvent event) {
        event.setJumpHeight(((EntityControl)this.module).jumpHeight.getValue());
    }
}
