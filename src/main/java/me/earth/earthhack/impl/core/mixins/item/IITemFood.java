/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemFood
 *  net.minecraft.potion.PotionEffect
 */
package me.earth.earthhack.impl.core.mixins.item;

import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ItemFood.class})
public interface IITemFood {
    @Accessor(value="potionId")
    public PotionEffect getPotionId();
}

