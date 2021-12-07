package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.Chat;

final class ListenerGameLoop
extends ModuleListener<Chat, GameLoopEvent> {
    public ListenerGameLoop(Chat module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        IGuiNewChat chat;
        if (!((Chat)this.module).cleared && ListenerGameLoop.mc.ingameGUI != null && (chat = (IGuiNewChat)((Object)ListenerGameLoop.mc.ingameGUI.getChatGUI())).getScrollPos() == 0) {
            ((Chat)this.module).clearNoScroll();
        }
    }
}
