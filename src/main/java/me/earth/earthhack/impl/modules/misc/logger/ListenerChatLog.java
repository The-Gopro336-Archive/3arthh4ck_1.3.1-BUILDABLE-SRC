package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.logger.Logger;

final class ListenerChatLog
extends ModuleListener<Logger, ChatEvent.Log> {
    public ListenerChatLog(Logger module) {
        super(module, ChatEvent.Log.class);
    }

    @Override
    public void invoke(ChatEvent.Log event) {
        if (((Logger)this.module).cancel) {
            event.setCancelled(true);
        }
    }
}
