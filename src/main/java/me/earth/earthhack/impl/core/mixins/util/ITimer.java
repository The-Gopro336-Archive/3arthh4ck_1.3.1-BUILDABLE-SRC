/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Timer
 */
package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Timer.class})
public interface ITimer {
    @Accessor(value="tickLength")
    public void setTickLength(float var1);
}

