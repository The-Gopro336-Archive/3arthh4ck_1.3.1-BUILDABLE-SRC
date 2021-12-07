/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiSubtitleOverlay
 *  net.minecraft.client.renderer.GlStateManager
 */
package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiSubtitleOverlay.class})
public abstract class MixinGuiSubtitleOverlay {
    @Inject(method={"renderSubtitles"}, at={@At(value="HEAD")})
    private void renderSubtitlesHook(CallbackInfo info) {
        Bus.EVENT_BUS.post(new Render2DEvent());
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

