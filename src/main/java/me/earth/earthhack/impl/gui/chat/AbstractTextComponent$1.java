package me.earth.earthhack.impl.gui.chat;

import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.util.text.TextComponentString;

final class AbstractTextComponent$1
extends AbstractTextComponent {
    AbstractTextComponent$1(String initial) {
        super(initial);
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String getUnformattedComponentText() {
        return "";
    }

    @Override
    public TextComponentString createCopy() {
        return EMPTY;
    }
}
