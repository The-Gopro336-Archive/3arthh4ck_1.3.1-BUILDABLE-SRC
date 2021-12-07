/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.forge.mixins.render;

import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ActiveRenderInfo.class})
public abstract class MixinActiveRenderInfo {
    @Inject(method={"updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V"}, at={@At(value="HEAD")}, remap=false)
    private static void updateRenderInfo(Entity entityplayerIn, boolean p_74583_1_, CallbackInfo ci) {
        RenderUtil.updateMatrices();
    }
}

