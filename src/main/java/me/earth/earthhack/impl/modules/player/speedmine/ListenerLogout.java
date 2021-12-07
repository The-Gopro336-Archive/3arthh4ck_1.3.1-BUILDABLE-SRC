/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;

final class ListenerLogout
extends ModuleListener<Speedmine, DisconnectEvent> {
    public ListenerLogout(Speedmine module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        mc.addScheduledTask(((Speedmine)this.module)::reset);
    }
}

