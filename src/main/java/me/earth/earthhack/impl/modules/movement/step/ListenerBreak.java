/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.step.Step;

final class ListenerBreak
extends ModuleListener<Step, BlockDestroyEvent> {
    public ListenerBreak(Step module) {
        super(module, BlockDestroyEvent.class);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        ((Step)this.module).onBreak();
    }
}

