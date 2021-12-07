package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.util.text.ITextComponent;

public interface IChatLine {
    public String getTimeStamp();

    public void setComponent(ITextComponent var1);
}
