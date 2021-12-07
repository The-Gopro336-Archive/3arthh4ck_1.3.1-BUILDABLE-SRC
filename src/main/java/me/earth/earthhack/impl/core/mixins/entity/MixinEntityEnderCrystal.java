/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityEnderCrystal.class})
public abstract class MixinEntityEnderCrystal
extends MixinEntity {
    @Inject(method={"<init>(Lnet/minecraft/world/World;DDD)V"}, at={@At(value="RETURN")})
    private void initHook(World worldIn, double x, double y, double z, CallbackInfo ci) {
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
    }
}

