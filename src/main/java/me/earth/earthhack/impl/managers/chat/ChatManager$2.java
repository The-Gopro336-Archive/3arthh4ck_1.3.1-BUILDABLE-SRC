package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class ChatManager$2
extends EventListener<WorldClientEvent.Load> {
    ChatManager$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        Globals.mc.addScheduledTask(() -> ChatManager.this.clear());
    }
}
