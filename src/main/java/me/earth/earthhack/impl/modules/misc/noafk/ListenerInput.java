/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;

final class ListenerInput
extends ModuleListener<NoAFK, MovementInputEvent> {
    public ListenerInput(NoAFK module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (((NoAFK)this.module).sneak.getValue().booleanValue()) {
            if (((NoAFK)this.module).sneak_timer.passed(2000L)) {
                ((NoAFK)this.module).sneaking = !((NoAFK)this.module).sneaking;
                ((NoAFK)this.module).sneak_timer.reset();
            }
            event.getInput().sneak = ((NoAFK)this.module).sneaking;
        }
    }
}

