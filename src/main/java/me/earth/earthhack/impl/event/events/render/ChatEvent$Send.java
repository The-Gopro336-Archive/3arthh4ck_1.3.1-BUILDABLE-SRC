package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import net.minecraft.util.text.ITextComponent;

public class ChatEvent$Send
extends ChatEvent {
    private ITextComponent chatComponent;
    private int chatLineId;
    private int updateCounter;
    private boolean displayOnly;

    public ChatEvent$Send(IGuiNewChat gui, ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        super(gui);
        this.chatComponent = chatComponent;
        this.chatLineId = chatLineId;
        this.updateCounter = updateCounter;
        this.displayOnly = displayOnly;
    }

    @Override
    public void invoke() {
        this.gui.invokeSetChatLine(this.chatComponent, this.chatLineId, this.updateCounter, this.displayOnly);
    }

    public ITextComponent getChatComponent() {
        return this.chatComponent;
    }

    public void setChatComponent(ITextComponent chatComponent) {
        this.chatComponent = chatComponent;
    }

    public int getChatLineId() {
        return this.chatLineId;
    }

    public void setChatLineId(int chatLineId) {
        this.chatLineId = chatLineId;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public void setUpdateCounter(int updateCounter) {
        this.updateCounter = updateCounter;
    }

    public boolean isDisplayOnly() {
        return this.displayOnly;
    }

    public void setDisplayOnly(boolean displayOnly) {
        this.displayOnly = displayOnly;
    }
}
