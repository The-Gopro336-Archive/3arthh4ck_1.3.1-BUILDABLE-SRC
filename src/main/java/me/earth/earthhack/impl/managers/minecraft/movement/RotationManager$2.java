package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;

class RotationManager$2
extends EventListener<MotionUpdateEvent> {
    RotationManager$2(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            RotationManager.this.set(event.getYaw(), event.getPitch());
        }
    }
}
