/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.gui.click.frame.impl;

import java.awt.Color;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class DescriptionFrame
extends Frame {
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private static final ResourceLocation LEFT_EAR = new ResourceLocation("earthhack:textures/gui/left_ear.png");
    private static final ResourceLocation RIGH_EAR = new ResourceLocation("earthhack:textures/gui/right_ear.png");
    private String description;

    public DescriptionFrame(float posX, float posY, float width, float height) {
        super("Description", posX, posY, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.description == null || !((ClickGui)DescriptionFrame.CLICK_GUI.get()).description.getValue().booleanValue()) {
            return;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        Color clr = ((ClickGui)DescriptionFrame.CLICK_GUI.get()).color.getValue();
        if (((ClickGui)DescriptionFrame.CLICK_GUI.get()).catEars.getValue().booleanValue()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(LEFT_EAR);
            GlStateManager.color((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)1.0f);
            Gui.drawScaledCustomSizeModalRect((int)((int)this.getPosX() - 8), (int)((int)this.getPosY() - 8), (float)0.0f, (float)0.0f, (int)20, (int)20, (int)20, (int)20, (float)20.0f, (float)20.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(RIGH_EAR);
            GlStateManager.color((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)1.0f);
            Gui.drawScaledCustomSizeModalRect((int)((int)(this.getPosX() + this.getWidth()) - 12), (int)((int)this.getPosY() - 8), (float)0.0f, (float)0.0f, (int)20, (int)20, (int)20, (int)20, (float)20.0f, (float)20.0f);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        Render2DUtil.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), ((ClickGui)DescriptionFrame.CLICK_GUI.get()).color.getValue().getRGB());
        Render2DUtil.drawBorderedRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0.5f, 0, -16777216);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel(), this.getPosX() + 3.0f, this.getPosY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
        float y = this.getPosY() + 2.0f + this.getHeight() / 2.0f + (float)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        List strings = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(this.getDescription(), (int)this.getWidth() - 1);
        Render2DUtil.drawRect(this.getPosX(), this.getPosY() + this.getHeight(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() + 3.0f + (float)((Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1) * strings.size()), -1845493760);
        for (String string : strings) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, this.getPosX() + 3.0f, y, -1);
            y += (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

