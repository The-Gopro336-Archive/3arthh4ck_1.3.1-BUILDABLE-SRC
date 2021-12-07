/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;

final class ListenerDisconnect
extends ModuleListener<Announcer, DisconnectEvent> {
    public ListenerDisconnect(Announcer module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        mc.addScheduledTask(((Announcer)this.module)::reset);
    }
}

