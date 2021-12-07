package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.util.text.event.HoverEvent;

public interface IHoverEvent {
    public HoverEvent setOffset(boolean var1);

    public boolean hasOffset();
}
