package me.earth.earthhack.impl.core.util;

import java.util.Iterator;
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
        Iterator iterator = this.base.iterator();
        while (iterator.hasNext()) {
            ITextComponent component = (ITextComponent)iterator.next();
            sb.append(component.getUnformattedComponentText());
        }
        return sb.toString();
    }
}
