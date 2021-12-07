/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.core.ducks.util;

import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;

public interface ITextComponentBase {
    public void setFormattingHook(Supplier<String> var1);

    public void setUnFormattedHook(Supplier<String> var1);

    public ITextComponent copyNoSiblings();
}

