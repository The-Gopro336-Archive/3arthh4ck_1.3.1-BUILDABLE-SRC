/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.datasync.DataParameter
 */
package me.earth.earthhack.impl.core.mixins.entity.living.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityPlayer.class})
public interface IEntityPlayer {
    @Accessor(value="ABSORPTION")
    public static DataParameter<Float> getAbsorption() {
        throw new IllegalStateException("ABSORPTION accessor wasn't shadowed.");
    }
}

