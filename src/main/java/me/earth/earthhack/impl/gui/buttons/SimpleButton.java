/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class SimpleButton
extends GuiButton {
    protected static final ResourceLocation LOCATION = new ResourceLocation("earthhack:textures/gui/gui_textures.png");
    protected final int textureX;
    protected final int textureY;
    protected final int hoveredX;
    protected final int hoveredY;

    public SimpleButton(int buttonID, int xPos, int yPos, int textureX, int textureY, int hoveredX, int hoveredY) {
        super(buttonID, xPos, yPos, 20, 20, "");
        this.textureX = textureX;
        this.textureY = textureY;
        this.hoveredX = hoveredX;
        this.hoveredY = hoveredY;
    }

    public void onClick(GuiScreen parent, int id) {
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(LOCATION);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.drawTexturedModalRect(this.x, this.y, this.hovered ? this.hoveredX : this.textureX, this.hovered ? this.hoveredY : this.textureY, this.width, this.height);
        }
    }
}

