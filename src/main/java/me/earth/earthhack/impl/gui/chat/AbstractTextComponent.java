/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.gui.chat;

import net.minecraft.util.text.TextComponentString;

public abstract class AbstractTextComponent
extends TextComponentString {
    public static final AbstractTextComponent EMPTY = new AbstractTextComponent(""){

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
    };
    private boolean wrap;

    public AbstractTextComponent(String initial) {
        super(initial);
    }

    public AbstractTextComponent setWrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }

    public boolean isWrapping() {
        return this.wrap;
    }

    public abstract String getText();

    public abstract String getUnformattedComponentText();

    public abstract TextComponentString createCopy();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractTextComponent)) {
            return false;
        }
        return this.getText().equals(((AbstractTextComponent)((Object)o)).getText());
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return "CustomComponent{text='" + this.getText() + '\'' + ", siblings=" + this.siblings + ", style=" + (Object)this.getStyle() + '}';
    }
}

