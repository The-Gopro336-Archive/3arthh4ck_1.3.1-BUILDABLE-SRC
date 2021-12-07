package me.earth.earthhack.impl.hud.watermark;

import java.awt.Color;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.renderer.GlStateManager;

public class Watermark
extends HudElement {
    public static String text = "3arthh4ck 1.3.1";

    public Watermark() {
        super("Watermark", false, 2.0f, 2.0f, Managers.TEXT.getStringWidth(text), Managers.TEXT.getStringHeight());
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        super.guiDraw(mouseX, mouseY, partialTicks);
        Managers.TEXT.drawString(text, this.getX(), this.getY(), Color.WHITE.getRGB(), true);
        GlStateManager.popMatrix();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        this.setWidth(Managers.TEXT.getStringWidth(text));
        this.setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        this.setWidth(Managers.TEXT.getStringWidth(text));
        this.setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void hudDraw(float partialTicks) {
        Managers.TEXT.drawString(text, this.getX(), this.getY(), Color.WHITE.getRGB(), true);
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }
}
