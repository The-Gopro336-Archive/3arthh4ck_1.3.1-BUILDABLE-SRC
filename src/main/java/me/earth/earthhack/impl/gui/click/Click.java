/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.gui.click;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.click.frame.impl.CategoryFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.DescriptionFrame;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Click
extends GuiScreen {
    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK = Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final ResourceLocation BLACK_PNG = new ResourceLocation("earthhack:textures/gui/black.png");
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    public static DescriptionFrame descriptionFrame = new DescriptionFrame(0.0f, 0.0f, 200.0f, 16.0f);
    private final ArrayList<Frame> frames = new ArrayList();
    private boolean oldVal = false;
    private boolean attached = false;
    private int emptyTicks = 0;

    public void init() {
        if (!this.attached) {
            ((ClickGui)Click.CLICK_GUI.get()).descriptionWidth.addObserver(e -> descriptionFrame.setWidth(((Integer)e.getValue()).intValue()));
            this.attached = true;
        }
        this.getFrames().clear();
        int x = ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue() != false ? 14 : 2;
        int y = ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue() != false ? 14 : 2;
        for (Category moduleCategory : Category.values()) {
            this.getFrames().add(new CategoryFrame(moduleCategory, (float)x, (float)y, 110.0f, 16.0f));
            if (x + 220 >= new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()) {
                x = ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue() != false ? 14 : 2;
                y += ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue() != false ? 32 : 20;
                continue;
            }
            x += ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue() != false ? 132 : 112;
        }
        descriptionFrame = new DescriptionFrame(x, y, ((ClickGui)Click.CLICK_GUI.get()).descriptionWidth.getValue().intValue(), 16.0f);
        this.getFrames().add(descriptionFrame);
        this.getFrames().forEach(Frame::init);
        this.oldVal = ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue();
    }

    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        this.init();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.mc.world == null) {
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
        }
        if (this.oldVal != ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue()) {
            this.init();
            this.oldVal = ((ClickGui)Click.CLICK_GUI.get()).catEars.getValue();
        }
        if (((ClickGui)Click.CLICK_GUI.get()).blur.getValue().booleanValue()) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            Render2DUtil.drawBlurryRect(0.0f, 0.0f, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), ((ClickGui)Click.CLICK_GUI.get()).blurAmount.getValue(), ((ClickGui)Click.CLICK_GUI.get()).blurSize.getValue());
        }
        this.getFrames().forEach(frame -> frame.drawScreen(mouseX, mouseY, partialTicks));
    }

    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        this.getFrames().forEach(frame -> frame.keyTyped(character, keyCode));
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.getFrames().forEach(frame -> frame.mouseClicked(mouseX, mouseY, mouseButton));
    }

    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.getFrames().forEach(frame -> frame.mouseReleased(mouseX, mouseY, mouseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.getFrames().forEach(frame -> {
            for (Component comp : frame.getComponents()) {
                if (!(comp instanceof ModuleComponent)) continue;
                ModuleComponent moduleComponent = (ModuleComponent)comp;
                for (Component component : moduleComponent.getComponents()) {
                    if (component instanceof KeybindComponent) {
                        KeybindComponent keybindComponent = (KeybindComponent)component;
                        keybindComponent.setBinding(false);
                    }
                    if (!(component instanceof StringComponent)) continue;
                    StringComponent stringComponent = (StringComponent)component;
                    stringComponent.setListening(false);
                }
            }
        });
    }

    public void onGuiOpened() {
        this.getFrames().forEach(frame -> {
            for (Component comp : frame.getComponents()) {
                if (!(comp instanceof ModuleComponent)) continue;
                ModuleComponent moduleComponent = (ModuleComponent)comp;
                for (Component component : moduleComponent.getComponents()) {
                    if (!(component instanceof ColorComponent)) continue;
                    ColorComponent colorComponent = (ColorComponent)component;
                    float[] hsb = Color.RGBtoHSB(colorComponent.getColorSetting().getRed(), colorComponent.getColorSetting().getGreen(), colorComponent.getColorSetting().getBlue(), null);
                    colorComponent.setHue(hsb[0]);
                    colorComponent.setSaturation(hsb[1]);
                    colorComponent.setBrightness(hsb[2]);
                    colorComponent.setAlpha((float)colorComponent.getColorSetting().getAlpha() / 255.0f);
                }
            }
        });
    }

    public ArrayList<Frame> getFrames() {
        return this.frames;
    }
}

