package me.earth.earthhack.impl.gui.chat.clickevents;

import java.util.function.Supplier;
import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import net.minecraft.util.text.event.ClickEvent;

public class SuppliedRunnableClickEvent
extends ClickEvent
implements IClickEvent {
    private final Supplier<Runnable> supplier;

    public SuppliedRunnableClickEvent(Supplier<Runnable> supplier) {
        super(ClickEvent.Action.RUN_COMMAND, "$runnable-supplied$");
        this.supplier = supplier;
    }

    @Override
    public void setRunnable(Runnable runnable) {
    }

    @Override
    public Runnable getRunnable() {
        return this.supplier.get();
    }
}
