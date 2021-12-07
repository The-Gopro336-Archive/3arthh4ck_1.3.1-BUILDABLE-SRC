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
