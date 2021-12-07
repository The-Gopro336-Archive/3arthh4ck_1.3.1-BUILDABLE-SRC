/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 */
package me.earth.earthhack.impl.managers.render;

import java.awt.Font;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.font.CustomFontRenderer;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import net.minecraft.client.renderer.GlStateManager;

public class TextRenderer
implements Globals {
    private final ModuleCache<FontMod> fontMod = Caches.getModule(FontMod.class);
    private CustomFontRenderer renderer = new CustomFontRenderer(new Font("Arial", 0, 17), true, true);

    public float drawStringWithShadow(String text, float x, float y, int color) {
        if (this.fontMod.isEnabled()) {
            return this.renderer.drawStringWithShadow(text, x, y, color);
        }
        return TextRenderer.mc.fontRenderer.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color) {
        if (this.fontMod.isEnabled()) {
            return this.renderer.drawString(text, x, y, color);
        }
        return TextRenderer.mc.fontRenderer.drawString(text, x, y, color, false);
    }

    public float drawString(String text, float x, float y, int color, boolean dropShadow) {
        if (this.fontMod.isEnabled()) {
            if (dropShadow) {
                return this.renderer.drawStringWithShadow(text, x, y, color);
            }
            return this.renderer.drawString(text, x, y, color);
        }
        return TextRenderer.mc.fontRenderer.drawString(text, x, y, color, dropShadow);
    }

    public void drawStringScaled(String text, float x, float y, int color, boolean dropShadow, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)scale, (float)scale, (float)scale);
        this.drawString(text, x / scale, y / scale, color, dropShadow);
        GlStateManager.scale((float)(1.0f / scale), (float)(1.0f / scale), (float)(1.0f / scale));
        GlStateManager.popMatrix();
    }

    public int getStringWidth(String text) {
        if (this.fontMod.isEnabled()) {
            return this.renderer.getStringWidth(text);
        }
        return TextRenderer.mc.fontRenderer.getStringWidth(text);
    }

    public float getStringWidthScaled(String text, float scale) {
        if (this.fontMod.isEnabled()) {
            return (float)this.renderer.getStringWidth(text) * scale;
        }
        return (float)TextRenderer.mc.fontRenderer.getStringWidth(text) * scale;
    }

    public float getStringHeight() {
        if (this.fontMod.isEnabled()) {
            return this.renderer.getHeight();
        }
        return TextRenderer.mc.fontRenderer.FONT_HEIGHT;
    }

    public float getStringHeight(float scale) {
        if (this.fontMod.isEnabled()) {
            return (float)this.renderer.getHeight() * scale;
        }
        return (float)TextRenderer.mc.fontRenderer.FONT_HEIGHT * scale;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean metrics) {
        this.renderer = new CustomFontRenderer(font, antiAlias, metrics);
    }
}

