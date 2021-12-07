/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;

final class ListenerSuffocation
extends ModuleListener<Phase, SuffocationEvent> {
    public ListenerSuffocation(Phase module) {
        super(module, SuffocationEvent.class);
    }

    @Override
    public void invoke(SuffocationEvent event) {
        event.setCancelled(true);
    }
}

