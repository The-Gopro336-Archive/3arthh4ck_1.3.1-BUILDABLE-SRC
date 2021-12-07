/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;

final class ListenerDisconnect
extends ModuleListener<LogoutSpots, DisconnectEvent> {
    public ListenerDisconnect(LogoutSpots module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        ((LogoutSpots)this.module).spots.clear();
    }
}

