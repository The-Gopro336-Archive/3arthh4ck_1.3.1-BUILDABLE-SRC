/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.Chat;

final class ListenerLogout
extends ModuleListener<Chat, DisconnectEvent> {
    public ListenerLogout(Chat module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        ((Chat)this.module).clearNoScroll();
    }
}

