package me.earth.earthhack.impl.gui.click.frame.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.BooleanComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.EnumComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.NumberComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class CategoryFrame
extends Frame {
    private final Category moduleCategory;
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private static final ResourceLocation LEFT_EAR = new ResourceLocation("earthhack:textures/gui/left_ear.png");
    private static final ResourceLocation RIGH_EAR = new ResourceLocation("earthhack:textures/gui/right_ear.png");

    public CategoryFrame(Category moduleCategory, float posX, float posY, float width, float height) {
        super(moduleCategory.name(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
        this.setExtended(true);
    }

    @Override
    public void init() {
        this.getComponents().clear();
        float offsetY = this.getHeight() + 1.0f;
        ArrayList<Module> moduleList = Managers.MODULES.getModulesFromCategory(this.getModuleCategory());
        moduleList.sort(Comparator.comparing(Module::getName));
        for (Module module : moduleList) {
            this.getComponents().add(new ModuleComponent(module, this.getPosX(), this.getPosY(), 0.0f, offsetY, this.getWidth(), 14.0f));
            offsetY += 14.0f;
        }
        super.init();
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        float scrollMaxHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        Color clr = ((ClickGui)CategoryFrame.CLICK_GUI.get()).color.getValue();
        if (((ClickGui)CategoryFrame.CLICK_GUI.get()).catEars.getValue().booleanValue()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(LEFT_EAR);
            GlStateManager.color((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)1.0f);
            Gui.drawScaledCustomSizeModalRect((int)((int)this.getPosX() - 8), (int)((int)this.getPosY() - 8), (float)0.0f, (float)0.0f, (int)20, (int)20, (int)20, (int)20, (float)20.0f, (float)20.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(RIGH_EAR);
            GlStateManager.color((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)1.0f);
            Gui.drawScaledCustomSizeModalRect((int)((int)(this.getPosX() + this.getWidth()) - 12), (int)((int)this.getPosY() - 8), (float)0.0f, (float)0.0f, (int)20, (int)20, (int)20, (int)20, (float)20.0f, (float)20.0f);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        Render2DUtil.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), ((ClickGui)CategoryFrame.CLICK_GUI.get()).color.getValue().getRGB());
        Render2DUtil.drawBorderedRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0.5f, 0, -16777216);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.getLabel(), this.getPosX() + 3.0f, this.getPosY() + this.getHeight() / 2.0f - (float)(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), -1);
        if (this.isExtended()) {
            if (RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getPosX(), this.getPosY() + this.getHeight(), this.getWidth(), Math.min(this.getScrollCurrentHeight(), scrollMaxHeight) + 1.0f) && this.getScrollCurrentHeight() > scrollMaxHeight) {
                float scrollSpeed = Math.min(this.getScrollCurrentHeight(), scrollMaxHeight) / (float)(Minecraft.getDebugFPS() >> 3);
                int wheel = Mouse.getDWheel();
                if (wheel < 0) {
                    if ((float)this.getScrollY() - scrollSpeed < -(this.getScrollCurrentHeight() - Math.min(this.getScrollCurrentHeight(), scrollMaxHeight))) {
                        this.setScrollY((int)(-(this.getScrollCurrentHeight() - Math.min(this.getScrollCurrentHeight(), scrollMaxHeight))));
                    } else {
                        this.setScrollY((int)((float)this.getScrollY() - scrollSpeed));
                    }
                } else if (wheel > 0) {
                    this.setScrollY((int)((float)this.getScrollY() + scrollSpeed));
                }
            }
            if (this.getScrollY() > 0) {
                this.setScrollY(0);
            }
            if (this.getScrollCurrentHeight() > scrollMaxHeight) {
                if ((float)(this.getScrollY() - 6) < -(this.getScrollCurrentHeight() - scrollMaxHeight)) {
                    this.setScrollY((int)(-(this.getScrollCurrentHeight() - scrollMaxHeight)));
                }
            } else if (this.getScrollY() < 0) {
                this.setScrollY(0);
            }
            Render2DUtil.drawRect(this.getPosX(), this.getPosY() + this.getHeight(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() + 1.0f + this.getCurrentHeight(), -1845493760);
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.scissor(this.getPosX(), this.getPosY() + this.getHeight() + 1.0f, this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() + scrollMaxHeight + 1.0f);
            this.getComponents().forEach(component -> component.drawScreen(mouseX, mouseY, partialTicks));
            GL11.glDisable(3089);
            GL11.glPopMatrix();
        }
        this.updatePositions();
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        float scrollMaxHeight = (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - this.getHeight();
        if (this.isExtended() && RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getPosX(), this.getPosY() + this.getHeight(), this.getWidth(), Math.min(this.getScrollCurrentHeight(), scrollMaxHeight) + 1.0f)) {
            this.getComponents().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void updatePositions() {
        float offsetY = this.getHeight() + 1.0f;
        for (Component component : this.getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
            if (component instanceof ModuleComponent && component.isExtended()) {
                for (Component component1 : ((ModuleComponent)component).getComponents()) {
                    if (component1 instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component1).getBooleanSetting())) {
                        offsetY += component1.getHeight();
                    }
                    if (component1 instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component1).getBindSetting())) {
                        offsetY += component1.getHeight();
                    }
                    if (component1 instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component1).getNumberSetting())) {
                        offsetY += component1.getHeight();
                    }
                    if (component1 instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component1).getEnumSetting())) {
                        offsetY += component1.getHeight();
                    }
                    if (component1 instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component1).getColorSetting())) {
                        offsetY += component1.getHeight();
                    }
                    if (!(component1 instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component1).getStringSetting())) continue;
                    offsetY += component1.getHeight();
                }
                offsetY += 3.0f;
            }
            offsetY += component.getHeight();
        }
    }

    private float getScrollCurrentHeight() {
        return this.getCurrentHeight() + this.getHeight() + 3.0f;
    }

    private float getCurrentHeight() {
        float cHeight = 1.0f;
        for (Component component : this.getComponents()) {
            if (component instanceof ModuleComponent && component.isExtended()) {
                for (Component component1 : ((ModuleComponent)component).getComponents()) {
                    if (component1 instanceof BooleanComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent)component1).getBooleanSetting())) {
                        cHeight += component1.getHeight();
                    }
                    if (component1 instanceof KeybindComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent)component1).getBindSetting())) {
                        cHeight += component1.getHeight();
                    }
                    if (component1 instanceof NumberComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent)component1).getNumberSetting())) {
                        cHeight += component1.getHeight();
                    }
                    if (component1 instanceof EnumComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent)component1).getEnumSetting())) {
                        cHeight += component1.getHeight();
                    }
                    if (component1 instanceof ColorComponent && Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent)component1).getColorSetting())) {
                        cHeight += component1.getHeight();
                    }
                    if (!(component1 instanceof StringComponent) || !Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent)component1).getStringSetting())) continue;
                    cHeight += component1.getHeight();
                }
                cHeight += 3.0f;
            }
            cHeight += component.getHeight();
        }
        return cHeight;
    }

    public Category getModuleCategory() {
        return this.moduleCategory;
    }
}
