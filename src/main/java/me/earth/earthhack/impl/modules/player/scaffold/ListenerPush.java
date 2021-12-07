/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;

final class ListenerPush
extends ModuleListener<Scaffold, BlockPushEvent> {
    public ListenerPush(Scaffold module) {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event) {
        if (((Scaffold)this.module).tower.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}

