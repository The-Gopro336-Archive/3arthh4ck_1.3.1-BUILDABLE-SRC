/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;

final class ListenerBlockPush
extends ModuleListener<Phase, BlockPushEvent> {
    public ListenerBlockPush(Phase module) {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event) {
        event.setCancelled(true);
    }
}

