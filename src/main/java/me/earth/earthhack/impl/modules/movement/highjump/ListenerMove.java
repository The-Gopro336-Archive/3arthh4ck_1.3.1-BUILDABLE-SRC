/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;

final class ListenerMove
extends ModuleListener<HighJump, MoveEvent> {
    public ListenerMove(HighJump module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (!Managers.NCP.passed(((HighJump)this.module).lagTime.getValue()) || !ListenerMove.mc.player.movementInput.jump || ((HighJump)this.module).onGround.getValue().booleanValue() && !ListenerMove.mc.player.onGround) {
            return;
        }
        if (((HighJump)this.module).explosions.getValue().booleanValue() || ((HighJump)this.module).velocity.getValue().booleanValue()) {
            if (((HighJump)this.module).motionY < ((HighJump)this.module).minY.getValue()) {
                return;
            }
            if (!((HighJump)this.module).timer.passed(((HighJump)this.module).delay.getValue().intValue())) {
                double d = ListenerMove.mc.player.motionY = ((HighJump)this.module).constant.getValue() != false ? ((HighJump)this.module).height.getValue() : ((HighJump)this.module).motionY * ((HighJump)this.module).factor.getValue();
                if (ListenerMove.mc.player.motionY < (double)0.42f) {
                    ListenerMove.mc.player.motionY = 0.42f;
                }
                event.setY(ListenerMove.mc.player.motionY);
                return;
            }
        }
        if (((HighJump)this.module).onlySpecial.getValue().booleanValue()) {
            return;
        }
        ListenerMove.mc.player.motionY = ((HighJump)this.module).height.getValue();
        event.setY(ListenerMove.mc.player.motionY);
    }
}

