/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.search.Search;

final class ListenerUnloadChunk
extends ModuleListener<Search, UnloadChunkEvent> {
    public ListenerUnloadChunk(Search module) {
        super(module, UnloadChunkEvent.class);
    }

    @Override
    public void invoke(UnloadChunkEvent event) {
        if (((Search)this.module).noUnloaded.getValue().booleanValue() && ListenerUnloadChunk.mc.world != null) {
            ((Search)this.module).toRender.keySet().removeIf(p -> !ListenerUnloadChunk.mc.world.isBlockLoaded(p));
        }
    }
}

