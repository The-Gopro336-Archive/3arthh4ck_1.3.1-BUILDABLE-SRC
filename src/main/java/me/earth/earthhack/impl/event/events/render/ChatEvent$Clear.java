package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.render.ChatEvent;

public class ChatEvent$Clear
extends ChatEvent {
    private boolean sent;

    public ChatEvent$Clear(IGuiNewChat gui, boolean sent) {
        super(gui);
    }

    @Override
    public void invoke() {
        this.gui.invokeClearChat(this.sent);
    }

    public boolean clearsSent() {
        return this.sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
