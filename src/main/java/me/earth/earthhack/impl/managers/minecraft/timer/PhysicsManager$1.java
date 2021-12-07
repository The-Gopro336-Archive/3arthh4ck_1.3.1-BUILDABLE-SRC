package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;

class PhysicsManager$1
extends EventListener<GameLoopEvent> {
    PhysicsManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (Globals.mc.player == null) {
            PhysicsManager.this.times = 0;
            return;
        }
        if (PhysicsManager.this.times > 0 && PhysicsManager.this.timer.passed(PhysicsManager.this.delay)) {
            PhysicsManager.this.blocking = true;
            while (PhysicsManager.this.times > 0) {
                PhysicsManager.this.invokePhysics();
                if (PhysicsManager.this.delay != 0) break;
                PhysicsManager.this.times--;
            }
            PhysicsManager.this.blocking = false;
            PhysicsManager.this.timer.reset();
        }
    }
}
