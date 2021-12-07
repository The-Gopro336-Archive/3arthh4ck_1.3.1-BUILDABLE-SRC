package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;

final class ObbyModule$1
extends EventListener<MotionUpdateEvent> {
    ObbyModule$1(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            HELPER.clearAllStates();
        }
    }
}
