/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.noglitchblocks;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;

final class ListenerBlockDestroy
extends ModuleListener<NoGlitchBlocks, BlockDestroyEvent> {
    public ListenerBlockDestroy(NoGlitchBlocks module) {
        super(module, BlockDestroyEvent.class, 1000);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        if (((NoGlitchBlocks)this.module).crack.getValue().booleanValue() && event.getStage() == Stage.PRE) {
            event.setCancelled(true);
        }
    }
}

