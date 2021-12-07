/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.DamageSource
 */
package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public interface ICachedDamage {
    public static final Setting<Boolean> SHOULD_CACHE = new BooleanSetting("CacheAttributes", true);

    public int getArmorValue();

    public float getArmorToughness();

    public int getExplosionModifier(DamageSource var1);

    default public boolean shouldCache() {
        return SHOULD_CACHE.getValue() != false && this instanceof EntityPlayer;
    }
}

