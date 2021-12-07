package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

class TimerManager$1
extends EventListener<TickEvent> {
    TimerManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        if (Globals.mc.player == null) {
            TimerManager.this.reset();
        } else {
            TimerManager.this.update();
        }
    }
}
