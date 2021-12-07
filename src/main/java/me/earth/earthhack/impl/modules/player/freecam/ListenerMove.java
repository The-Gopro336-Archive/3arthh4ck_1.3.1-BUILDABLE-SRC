/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;

final class ListenerMove
extends ModuleListener<Freecam, MoveEvent> {
    public ListenerMove(Freecam module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ListenerMove.mc.player.noClip = true;
    }
}

