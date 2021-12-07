/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.ClickEvent$Action
 */
package me.earth.earthhack.impl.gui.chat.clickevents;

import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import net.minecraft.util.text.event.ClickEvent;

public class RunnableClickEvent
extends ClickEvent {
    public RunnableClickEvent(Runnable runnable) {
        super(ClickEvent.Action.RUN_COMMAND, "$runnable$");
        ((IClickEvent)((Object)this)).setRunnable(runnable);
    }
}

