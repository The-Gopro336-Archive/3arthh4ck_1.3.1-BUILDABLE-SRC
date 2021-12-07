package me.earth.earthhack.impl.gui.chat.components;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.earth.earthhack.impl.core.ducks.util.IHoverable;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class SuppliedHoverableComponent
extends SuppliedComponent
implements IHoverable {
    private final BooleanSupplier canBeHovered;

    public SuppliedHoverableComponent(Supplier<String> supplier, BooleanSupplier canBeHovered) {
        super(supplier);
        this.canBeHovered = canBeHovered;
    }

    @Override
    public boolean canBeHovered() {
        return this.canBeHovered.getAsBoolean();
    }

    @Override
    public TextComponentString createCopy() {
        SuppliedHoverableComponent copy = new SuppliedHoverableComponent(this.supplier, this.canBeHovered);
        copy.setStyle(this.getStyle().createShallowCopy());
        for (ITextComponent component : this.getSiblings()) {
            copy.appendSibling(component.createCopy());
        }
        return copy;
    }
}
