/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EatEvent {
    private final ItemStack stack;
    private final EntityLivingBase entity;

    public EatEvent(ItemStack stack, EntityLivingBase entity) {
        this.stack = stack;
        this.entity = entity;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }
}

