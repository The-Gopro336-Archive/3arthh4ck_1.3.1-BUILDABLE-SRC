/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderMinecart
 */
package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.RenderMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderMinecart.class})
public abstract class MixinRenderMinecart {
    @Inject(method={"renderCartContents"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderCartContentsHook(CallbackInfo info) {
        if (ESP.isRendering) {
            info.cancel();
        }
    }
}

