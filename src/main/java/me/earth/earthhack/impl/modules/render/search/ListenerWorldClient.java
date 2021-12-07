/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.search.Search;

final class ListenerWorldClient
extends ModuleListener<Search, WorldClientEvent.Load> {
    public ListenerWorldClient(Search module) {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        ((Search)this.module).toRender.clear();
    }
}

