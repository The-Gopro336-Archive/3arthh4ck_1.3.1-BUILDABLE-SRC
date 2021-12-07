/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;

public class ListenerTick
extends ModuleListener<Phase, TickEvent> {
    public ListenerTick(Phase module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((Phase)this.module).mode.getValue() != PhaseMode.ConstantiamNew || ListenerTick.mc.player.collidedHorizontally) {
            // empty if block
        }
    }
}

