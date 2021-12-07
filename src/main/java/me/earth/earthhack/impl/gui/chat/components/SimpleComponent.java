/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class SimpleComponent
extends AbstractTextComponent {
    private final String text;

    public SimpleComponent(String initial) {
        super(initial);
        this.text = initial;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getUnformattedComponentText() {
        return this.text;
    }

    @Override
    public TextComponentString createCopy() {
        SimpleComponent copy = new SimpleComponent(this.text);
        copy.setStyle(this.getStyle().createShallowCopy());
        for (ITextComponent sibling : this.getSiblings()) {
            copy.appendSibling(sibling.createCopy());
        }
        return copy;
    }
}

