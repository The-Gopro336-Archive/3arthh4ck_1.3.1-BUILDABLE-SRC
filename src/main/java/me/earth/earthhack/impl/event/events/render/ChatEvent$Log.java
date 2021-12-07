package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.render.ChatEvent;

public class ChatEvent$Log
extends ChatEvent {
    public ChatEvent$Log(IGuiNewChat gui) {
        super(gui);
    }

    @Override
    public void invoke() {
    }
}
