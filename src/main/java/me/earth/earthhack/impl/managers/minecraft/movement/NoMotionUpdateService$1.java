package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;

class NoMotionUpdateService$1
extends EventListener<MotionUpdateEvent> {
    NoMotionUpdateService$1(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            NoMotionUpdateService.this.setAwaiting(true);
        } else {
            if (NoMotionUpdateService.this.isAwaiting()) {
                Bus.EVENT_BUS.post(new NoMotionUpdateEvent());
            }
            NoMotionUpdateService.this.setAwaiting(false);
        }
    }
}
