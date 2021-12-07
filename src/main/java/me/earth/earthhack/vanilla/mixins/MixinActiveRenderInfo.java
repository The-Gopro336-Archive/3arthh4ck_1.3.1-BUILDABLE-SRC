/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.vanilla.mixins;

import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ActiveRenderInfo.class})
public abstract class MixinActiveRenderInfo {
    @Inject(method={"updateRenderInfo(Lnet/minecraft/entity/player/EntityPlayer;Z)V"}, at={@At(value="HEAD")})
    private static void updateRenderInfo(EntityPlayer entityplayerIn, boolean p_74583_1_, CallbackInfo ci) {
        RenderUtil.updateMatrices();
    }
}

