/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.commands.gui;

import java.io.IOException;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.commands.gui.CommandChatGui;
import me.earth.earthhack.impl.commands.gui.ExitButton;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class CommandGui
extends GuiScreen {
    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK = Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final ResourceLocation BLACK_PNG = new ResourceLocation("earthhack:textures/gui/black.png");
    private final CommandChatGui chat = new CommandChatGui();
    private final GuiScreen parent;
    private final int id;

    public CommandGui(GuiScreen parent, int id) {
        this.parent = parent;
        this.id = id;
    }

    public void setText(String text) {
        this.chat.setText(text);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            super.keyTyped(typedChar, keyCode);
            return;
        }
        this.chat.keyTyped(typedChar, keyCode);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.chat.handleMouseInput();
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        this.buttonList.clear();
        this.buttonList.add(new ExitButton(0, this.width - 24, 5));
        this.chat.setWorldAndResolution(mc, width, height);
        this.chat.setFocused(true);
        this.chat.setText(Commands.getPrefix());
    }

    public void onGuiClosed() {
        this.chat.onGuiClosed();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(this.mc);
        if (BACK.getValue().booleanValue()) {
            this.drawDefaultBackground();
        } else {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(BLACK_PNG);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0, (double)this.height, 0.0).tex(0.0, (double)((float)this.height / 32.0f + 0.0f)).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos((double)this.width, (double)this.height, 0.0).tex((double)((float)this.width / 32.0f), (double)((float)this.height / 32.0f + 0.0f)).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos((double)this.width, 0.0, 0.0).tex((double)((float)this.width / 32.0f), 0.0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
            tessellator.draw();
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)0.0f, (float)(res.getScaledHeight() - 48), (float)0.0f);
        this.mc.ingameGUI.getChatGUI().drawChat(this.mc.ingameGUI.getUpdateCounter());
        GlStateManager.popMatrix();
        this.chat.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.parent.confirmClicked(false, this.id);
        }
    }
}

