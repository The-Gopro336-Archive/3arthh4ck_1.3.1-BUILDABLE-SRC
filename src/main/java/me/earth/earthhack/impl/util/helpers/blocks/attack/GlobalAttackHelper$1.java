package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;

class GlobalAttackHelper$1
extends EventListener<MotionUpdateEvent> {
    GlobalAttackHelper$1(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            GlobalAttackHelper.this.attacked = false;
            GlobalAttackHelper.this.noCrystal = false;
            GlobalAttackHelper.this.invalid = false;
        }
    }
}
