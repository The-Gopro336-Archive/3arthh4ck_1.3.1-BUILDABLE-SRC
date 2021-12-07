/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentBase
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.gui.chat.components;

import java.util.function.Supplier;
import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import me.earth.earthhack.impl.core.util.SimpleTextFormatHook;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

public class SuppliedComponent
extends AbstractTextComponent {
    protected final Supplier<String> supplier;

    public SuppliedComponent(Supplier<String> supplier) {
        super(supplier.get());
        this.supplier = supplier;
        ((ITextComponentBase)((Object)this)).setFormattingHook(new SimpleTextFormatHook((TextComponentBase)this));
        ((ITextComponentBase)((Object)this)).setUnFormattedHook(new SimpleTextFormatHook((TextComponentBase)this));
    }

    @Override
    public String getText() {
        return this.supplier.get();
    }

    @Override
    public String getUnformattedComponentText() {
        return this.supplier.get();
    }

    @Override
    public TextComponentString createCopy() {
        SuppliedComponent copy = new SuppliedComponent(this.supplier);
        copy.setStyle(this.getStyle().createShallowCopy());
        for (ITextComponent component : this.getSiblings()) {
            copy.appendSibling(component.createCopy());
        }
        return copy;
    }
}

