/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.item.ItemStack;

public class ProcessRightClickItemEvent {
    private final ItemStack itemStack;

    public ProcessRightClickItemEvent(ItemStack stack) {
        this.itemStack = stack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}

