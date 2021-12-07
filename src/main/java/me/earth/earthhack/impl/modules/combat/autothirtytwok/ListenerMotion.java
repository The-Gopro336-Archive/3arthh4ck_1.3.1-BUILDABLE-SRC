package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;

final class ListenerMotion
extends ModuleListener<Auto32k, MotionUpdateEvent> {
    public ListenerMotion(Auto32k module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        ((Auto32k)this.module).onUpdateWalkingPlayer(event);
    }
}
