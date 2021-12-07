/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.EntityWolf
 */
package me.earth.earthhack.impl.core.mixins.entity.living;

import net.minecraft.entity.passive.EntityWolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityWolf.class})
public interface IEntityWolf {
    @Accessor(value="isWet")
    public boolean getIsWet();

    @Accessor(value="isWet")
    public void setIsWet(boolean var1);
}

