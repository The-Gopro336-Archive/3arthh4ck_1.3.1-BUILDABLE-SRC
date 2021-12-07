package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.util.text.ITextComponent;

public interface IGuiNewChat {
    public boolean replace(ITextComponent var1, int var2, boolean var3, boolean var4);

    public int getScrollPos();

    public void setScrollPos(int var1);

    public boolean getScrolled();

    public void setScrolled(boolean var1);

    public void invokeSetChatLine(ITextComponent var1, int var2, int var3, boolean var4);

    public void invokeClearChat(boolean var1);
}
