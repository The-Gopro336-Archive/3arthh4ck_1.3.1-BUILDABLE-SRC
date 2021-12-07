package me.earth.earthhack.impl.gui.chat.clickevents;

import net.minecraft.util.text.event.ClickEvent;

public abstract class SmartClickEvent
extends ClickEvent {
    public SmartClickEvent(ClickEvent.Action theAction) {
        super(theAction, "$smart_click_value$");
    }

    public abstract String getValue();

    @Override
    public boolean equals(Object o) {
        if (o instanceof SmartClickEvent) {
            return super.equals(o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 1;
    }
}
