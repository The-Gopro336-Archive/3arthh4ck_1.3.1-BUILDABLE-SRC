package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

class ChatManager$1
extends EventListener<DisconnectEvent> {
    ChatManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        Globals.mc.addScheduledTask(() -> ChatManager.this.clear());
    }
}
