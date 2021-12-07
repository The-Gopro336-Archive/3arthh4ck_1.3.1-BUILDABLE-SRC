/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;

final class ListenerOverlay
extends ModuleListener<Freecam, SuffocationEvent> {
    public ListenerOverlay(Freecam module) {
        super(module, SuffocationEvent.class);
    }

    @Override
    public void invoke(SuffocationEvent event) {
        event.setCancelled(true);
    }
}

