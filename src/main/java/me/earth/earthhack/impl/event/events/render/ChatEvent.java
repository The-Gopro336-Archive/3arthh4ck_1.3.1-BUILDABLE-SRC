package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import net.minecraft.util.text.ITextComponent;

public abstract class ChatEvent
extends Event {
    protected final IGuiNewChat gui;

    public ChatEvent(IGuiNewChat gui) {
        this.gui = gui;
    }

    public abstract void invoke();

    public static class Send
    extends ChatEvent {
        private ITextComponent chatComponent;
        private int chatLineId;
        private int updateCounter;
        private boolean displayOnly;

        public Send(IGuiNewChat gui, ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
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

    public static class Log
    extends ChatEvent {
        public Log(IGuiNewChat gui) {
            super(gui);
        }

        @Override
        public void invoke() {
        }
    }

    public static class Clear
    extends ChatEvent {
        private boolean sent;

        public Clear(IGuiNewChat gui, boolean sent) {
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
}
