/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;

final class ListenerTick
extends ModuleListener<Jesus, TickEvent> {
    public ListenerTick(Jesus module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((Jesus)this.module).timer.passed(800L) && ListenerTick.mc.player != null && ((Jesus)this.module).mode.getValue() == JesusMode.Solid) {
            if (ListenerTick.mc.player.fallDistance > 3.0f) {
                return;
            }
            if ((ListenerTick.mc.player.isInLava() || ListenerTick.mc.player.isInWater()) && !ListenerTick.mc.player.isSneaking()) {
                ListenerTick.mc.player.motionY = 0.1;
                return;
            }
            if (PositionUtil.inLiquid() && !ListenerTick.mc.player.isSneaking()) {
                ListenerTick.mc.player.motionY = 0.1;
            }
        }
    }
}

