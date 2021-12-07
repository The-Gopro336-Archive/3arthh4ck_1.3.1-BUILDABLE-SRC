package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import net.minecraft.item.ItemStack;

public class ToolTipEvent$Post
extends ToolTipEvent {
    public ToolTipEvent$Post(ItemStack stack, int x, int y) {
        super(stack, x, y);
    }
}
