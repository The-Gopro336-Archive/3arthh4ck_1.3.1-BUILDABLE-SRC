package me.earth.earthhack.impl.core.ducks.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ITileEntityShulkerBox {
    public NonNullList<ItemStack> getItems();
}
