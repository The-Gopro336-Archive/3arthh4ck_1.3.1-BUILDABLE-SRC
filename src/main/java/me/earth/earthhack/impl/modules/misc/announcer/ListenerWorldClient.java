/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;

final class ListenerWorldClient
extends ModuleListener<Announcer, WorldClientEvent> {
    public ListenerWorldClient(Announcer module) {
        super(module, WorldClientEvent.class);
    }

    @Override
    public void invoke(WorldClientEvent event) {
        ((Announcer)this.module).reset();
    }
}

