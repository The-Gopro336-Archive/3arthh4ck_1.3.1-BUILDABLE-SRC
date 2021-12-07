/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.network.PhysicsUtil;

final class ListenerMotion
extends ModuleListener<Timer, MotionUpdateEvent> {
    private float offset = 4.0E-4f;

    public ListenerMotion(Timer module) {
        super(module, MotionUpdateEvent.class, -500000);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            if (((Timer)this.module).mode.getValue() == TimerMode.Blink && (double)(event.getPitch() - ((IEntityPlayerSP)ListenerMotion.mc.player).getLastReportedPitch()) == 0.0 && (double)(event.getYaw() - ((IEntityPlayerSP)ListenerMotion.mc.player).getLastReportedYaw()) == 0.0) {
                this.offset = -this.offset;
                event.setYaw(event.getYaw() + this.offset);
                event.setPitch(event.getPitch() + this.offset);
            }
        } else if (event.getStage() == Stage.POST) {
            if (((Timer)this.module).autoOff.getValue() != 0 && ((Timer)this.module).offTimer.passed(((Timer)this.module).autoOff.getValue().intValue())) {
                ((Timer)this.module).disable();
                return;
            }
            if (((Timer)this.module).ticks < ((Timer)this.module).updates.getValue() && ((Timer)this.module).mode.getValue() == TimerMode.Physics) {
                ++((Timer)this.module).ticks;
                PhysicsUtil.runPhysicsTick();
            } else {
                ((Timer)this.module).ticks = 0;
            }
        }
    }
}

