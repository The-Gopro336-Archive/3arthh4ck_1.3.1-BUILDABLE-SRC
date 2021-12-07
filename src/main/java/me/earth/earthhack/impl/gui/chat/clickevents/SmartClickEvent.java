/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.ClickEvent$Action
 */
package me.earth.earthhack.impl.gui.chat.clickevents;

import net.minecraft.util.text.event.ClickEvent;

public abstract class SmartClickEvent
extends ClickEvent {
    public SmartClickEvent(ClickEvent.Action theAction) {
        super(theAction, "$smart_click_value$");
    }

    public abstract String getValue();

    public boolean equals(Object o) {
        if (o instanceof SmartClickEvent) {
            return super.equals(o);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() + 1;
    }
}

