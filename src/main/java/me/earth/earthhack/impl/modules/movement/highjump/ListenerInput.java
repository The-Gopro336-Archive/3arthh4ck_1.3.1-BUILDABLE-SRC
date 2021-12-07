/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;

final class ListenerInput
extends ModuleListener<HighJump, MovementInputEvent> {
    public ListenerInput(HighJump module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (((HighJump)this.module).onlySpecial.getValue().booleanValue() && (((HighJump)this.module).explosions.getValue().booleanValue() || ((HighJump)this.module).velocity.getValue().booleanValue()) && ((HighJump)this.module).cancelJump.getValue().booleanValue() && ((HighJump)this.module).motionY < ((HighJump)this.module).minY.getValue()) {
            event.getInput().jump = false;
        }
    }
}

