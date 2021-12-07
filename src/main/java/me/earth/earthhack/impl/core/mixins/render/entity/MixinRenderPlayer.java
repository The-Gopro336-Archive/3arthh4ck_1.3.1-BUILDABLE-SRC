/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderPlayer
 */
package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.handchams.HandChams;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderPlayer.class})
public abstract class MixinRenderPlayer {
    private static final ModuleCache<Nametags> NAMETAGS = Caches.getModule(Nametags.class);
    private static final ModuleCache<HandChams> HAND_CHAMS = Caches.getModule(HandChams.class);

    @Inject(method={"renderEntityName"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderEntityNameHook(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (NAMETAGS.isEnabled()) {
            info.cancel();
        }
    }

    @Redirect(method={"renderRightArm"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderRightArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HAND_CHAMS.isEnabled() && ((HandChams)MixinRenderPlayer.HAND_CHAMS.get()).mode.getValue() == ChamsMode.Gradient) {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)((HandChams)MixinRenderPlayer.HAND_CHAMS.get()).color.getValue().getAlpha() / 255.0f));
        }
    }

    @Redirect(method={"renderLeftArm"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderLeftArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HAND_CHAMS.isEnabled() && ((HandChams)MixinRenderPlayer.HAND_CHAMS.get()).mode.getValue() == ChamsMode.Gradient) {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)((HandChams)MixinRenderPlayer.HAND_CHAMS.get()).color.getValue().getAlpha() / 255.0f));
        }
    }
}

