package me.earth.earthhack.impl.modules.combat.bedbomb;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;

class BedBomb$1
extends EventListener<MotionUpdateEvent> {
    BedBomb$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        BedBomb.this.onUpdateWalkingPlayer(event);
    }
}
