/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.misc.tooltips.util;

import net.minecraft.item.ItemStack;

public class TimeStack {
    private final ItemStack stack;
    private final long time;

    public TimeStack(ItemStack stack, long time) {
        this.stack = stack;
        this.time = time;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public long getTime() {
        return this.time;
    }
}

