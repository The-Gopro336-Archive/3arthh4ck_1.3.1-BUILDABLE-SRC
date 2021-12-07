/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentBase
 */
package me.earth.earthhack.impl.core.util;

import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public class SimpleTextFormatHook
implements Supplier<String> {
    private final TextComponentBase base;

    public SimpleTextFormatHook(TextComponentBase base) {
        this.base = base;
    }

    @Override
    public String get() {
        StringBuilder sb = new StringBuilder();
        for (ITextComponent component : this.base) {
            sb.append(component.getUnformattedComponentText());
        }
        return sb.toString();
    }
}

