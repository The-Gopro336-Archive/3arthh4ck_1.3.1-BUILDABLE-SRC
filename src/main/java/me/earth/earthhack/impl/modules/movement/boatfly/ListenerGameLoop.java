/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.entity.Entity;

final class ListenerGameLoop
extends ModuleListener<BoatFly, GameLoopEvent> {
    public ListenerGameLoop(BoatFly module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (ListenerGameLoop.mc.player == null) {
            return;
        }
        Entity riding = ListenerGameLoop.mc.player.getRidingEntity();
        if (riding == null) {
            return;
        }
        if (ListenerGameLoop.mc.player.equals((Object)riding.getControllingPassenger())) {
            double d = ListenerGameLoop.mc.player.movementInput.jump ? ((BoatFly)this.module).upSpeed.getValue() : (riding.motionY = KeyBoardUtil.isKeyDown(((BoatFly)this.module).downBind) ? -((BoatFly)this.module).downSpeed.getValue().doubleValue() : (double)((BoatFly)this.module).glide.getValue().floatValue());
            if (((BoatFly)this.module).fixYaw.getValue().booleanValue()) {
                riding.rotationYaw = ListenerGameLoop.mc.player.rotationYaw;
            }
        }
    }
}

