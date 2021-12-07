/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.Chat;

final class ListenerChat
extends ModuleListener<Chat, ChatEvent.Send> {
    public ListenerChat(Chat module) {
        super(module, ChatEvent.Send.class);
    }

    @Override
    public void invoke(ChatEvent.Send event) {
        IGuiNewChat chat;
        if (((Chat)this.module).noScroll.getValue().booleanValue() && ListenerChat.mc.ingameGUI != null && (chat = (IGuiNewChat)ListenerChat.mc.ingameGUI.getChatGUI()).getScrollPos() != 0) {
            ((Chat)this.module).events.add(event);
            ((Chat)this.module).cleared = false;
            event.setCancelled(true);
        }
    }
}

