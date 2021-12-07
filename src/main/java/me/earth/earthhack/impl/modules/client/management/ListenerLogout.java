/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.management.Management;

final class ListenerLogout
extends ModuleListener<Management, DisconnectEvent> {
    public ListenerLogout(Management module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        if (((Management)this.module).logout.getValue().booleanValue()) {
            Managers.COMBAT.reset();
        }
    }
}

