package me.earth.earthhack.impl.core.mixins.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={GuiContainer.class})
public interface IGuiContainer {
    @Accessor(value="hoveredSlot")
    public Slot getHoveredSlot();
}
