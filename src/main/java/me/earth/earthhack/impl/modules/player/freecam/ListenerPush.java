/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;

final class ListenerPush
extends ModuleListener<Freecam, BlockPushEvent> {
    public ListenerPush(Freecam module) {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event) {
        event.setCancelled(true);
    }
}

