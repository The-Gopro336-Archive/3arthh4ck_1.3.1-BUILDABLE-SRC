package me.earth.earthhack.impl.gui.hud;

import java.awt.Color;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.AbstractGuiElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;

public class HudElementButton
extends AbstractGuiElement {
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private final HudElement element;

    public HudElementButton(HudElement element) {
        super(element.getName(), Managers.TEXT.getStringWidth(element.getName()), Managers.TEXT.getStringHeight());
        this.element = element;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (this.element.isEnabled()) {
            Render2DUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), ((ClickGui)HudElementButton.CLICK_GUI.get()).color.getValue().getRGB());
            Managers.TEXT.drawString(this.element.getName(), this.getX() + this.getWidth() / 2.0f - (float)Managers.TEXT.getStringWidth(this.element.getName()) / 2.0f, this.getY(), Color.WHITE.getRGB());
        } else {
            Managers.TEXT.drawString(this.element.getName(), this.getX() + this.getWidth() / 2.0f - (float)Managers.TEXT.getStringWidth(this.element.getName()) / 2.0f, this.getY(), Color.GRAY.getRGB());
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isHovered(this, mouseX, mouseY) && mouseButton == 0) {
            this.element.toggle();
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyPressed(char eventChar, int key) {
    }

    public HudElement getElement() {
        return this.element;
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }
}
