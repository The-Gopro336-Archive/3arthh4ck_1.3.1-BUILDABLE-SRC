/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.replenish.Replenish;

final class ListenerWorldClient
extends ModuleListener<Replenish, WorldClientEvent> {
    public ListenerWorldClient(Replenish module) {
        super(module, WorldClientEvent.class);
    }

    @Override
    public void invoke(WorldClientEvent event) {
        ((Replenish)this.module).clear();
    }
}

