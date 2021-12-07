package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.util.math.MathUtil;

class SpeedManager$1
extends EventListener<TickEvent> {
    SpeedManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe() && SpeedManager.this.timer.passed(40L)) {
            SpeedManager.this.speed = MathUtil.distance2D(Globals.mc.player.getPositionVector(), SpeedManager.this.last);
            SpeedManager.this.last = Globals.mc.player.getPositionVector();
            SpeedManager.this.timer.reset();
        }
    }
}
