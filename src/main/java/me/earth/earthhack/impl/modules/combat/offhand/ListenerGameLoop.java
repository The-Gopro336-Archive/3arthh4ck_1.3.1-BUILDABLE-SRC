/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;

final class ListenerGameLoop
extends ModuleListener<Offhand, GameLoopEvent> {
    public ListenerGameLoop(Offhand module) {
        super(module, GameLoopEvent.class, Integer.MAX_VALUE);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        ((Offhand)this.module).doOffhand();
    }
}

