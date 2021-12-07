/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.gui.hud;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.AbstractGuiElement;
import me.earth.earthhack.impl.gui.hud.HudElementButton;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class HudPanel
extends AbstractGuiElement {
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private boolean dragging;
    private boolean hovered;
    private boolean stretching;
    private GuiUtil.Edge currentEdge;
    private float draggingX;
    private float draggingY;
    private float stretchingWidth;
    private float stretchingHeight;
    private float stretchingX;
    private float stretchingY;
    private float stretchingX2;
    private float stretchingY2;
    private float scrollOffset;
    private float elementOffset = 20.0f;
    private final Set<HudElementButton> elementButtons = new HashSet<HudElementButton>();

    public HudPanel() {
        super("HudPanel", 200.0f, 200.0f, 100.0f, 300.0f);
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            this.elementButtons.add(new HudElementButton(element));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        this.hovered = GuiUtil.isHovered(this, mouseX, mouseY);
        if (this.dragging) {
            this.setX((float)mouseX - this.draggingX);
            this.setY((float)mouseY - this.draggingY);
        }
        if (this.stretching && this.currentEdge != null) {
            switch (this.currentEdge) {
                case BOTTOM: {
                    this.setHeight(this.stretchingHeight + ((float)mouseY - this.stretchingY));
                    break;
                }
                case LEFT: {
                    this.setX(this.stretchingX + ((float)mouseX - this.stretchingX));
                    this.setWidth(this.stretchingX2 - this.getX());
                    break;
                }
                case RIGHT: {
                    this.setWidth(this.stretchingWidth + ((float)mouseX - this.stretchingX));
                    break;
                }
                case BOTTOM_LEFT: {
                    this.setHeight(this.stretchingHeight + ((float)mouseY - this.stretchingY));
                    this.setX(this.stretchingX + ((float)mouseX - this.stretchingX));
                    this.setWidth(this.stretchingX2 - this.getX());
                    break;
                }
                case BOTTOM_RIGHT: {
                    this.setHeight(this.stretchingHeight + ((float)mouseY - this.stretchingY));
                    this.setWidth(this.stretchingWidth + ((float)mouseX - this.stretchingX));
                }
            }
        }
        if (this.getX() <= 0.0f) {
            this.setX(0.0f);
        }
        if (this.getWidth() <= 100.0f) {
            this.setWidth(100.0f);
        }
        if (this.getHeight() <= 200.0f) {
            this.setHeight(200.0f);
        }
        if (this.getY() <= 0.0f) {
            this.setY(0.0f);
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib((int)1048575);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Render2DUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -1845493760);
        Render2DUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + Managers.TEXT.getStringHeight(1.0f) + 10.0f, ((ClickGui)HudPanel.CLICK_GUI.get()).color.getValue().getRGB());
        Managers.TEXT.drawStringScaled("Hud Elements", this.getX() + this.getWidth() / 2.0f - Managers.TEXT.getStringWidthScaled("Hud Elements", 1.0f) / 2.0f, this.getY() + 5.0f, Color.WHITE.getRGB(), true, 1.0f);
        float yOffset = 0.0f;
        RenderUtil.scissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        GL11.glEnable((int)3089);
        for (HudElementButton button : this.elementButtons) {
            button.setX(this.getX());
            button.setWidth(this.getWidth());
            button.setY(this.getY() + Managers.TEXT.getStringHeight() + 12.0f + yOffset);
            button.draw(mouseX, mouseY, partialTicks);
            yOffset += button.getHeight() + 1.0f;
        }
        GL11.glDisable((int)3089);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.currentEdge = GuiUtil.getHoveredEdge(this, mouseX, mouseY, 5);
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            if (this.currentEdge != null) {
                this.stretching = true;
                this.dragging = false;
                this.stretchingWidth = this.getWidth();
                this.stretchingHeight = this.getHeight();
                this.stretchingX = mouseX;
                this.stretchingY = mouseY;
                this.stretchingX2 = this.getX() + this.getWidth();
                this.stretchingY2 = this.getY() + this.getHeight();
            } else if (GuiUtil.isHovered(this.getX(), this.getY(), this.getWidth(), 20.0f, (float)mouseX, (float)mouseY)) {
                this.dragging = true;
                this.stretching = false;
                this.draggingX = (float)mouseX - this.getX();
                this.draggingY = (float)mouseY - this.getY();
            }
        }
        for (HudElementButton button : this.elementButtons) {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        this.stretching = false;
        this.currentEdge = null;
        for (HudElementButton button : this.elementButtons) {
            button.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public void keyPressed(char eventChar, int key) {
        for (HudElementButton button : this.elementButtons) {
            button.keyPressed(eventChar, key);
        }
    }

    public void mouseScrolled() {
    }

    public Set<HudElementButton> getElementButtons() {
        return this.elementButtons;
    }
}

