package me.earth.earthhack.impl.gui.click.frame;

import java.util.ArrayList;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Frame {
    private final String label;
    private float posX;
    private float posY;
    private float lastPosX;
    private float lastPosY;
    private float width;
    private final float height;
    private boolean extended;
    private boolean dragging;
    private final ArrayList<Component> components = new ArrayList();
    private int scrollY;

    public Frame(String label, float posX, float posY, float width, float height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void init() {
        this.components.forEach(Component::init);
    }

    public void moved(float posX, float posY) {
        this.components.forEach(component -> component.moved(posX, posY));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (this.isDragging()) {
            this.setPosX((float)mouseX + this.getLastPosX());
            this.setPosY((float)mouseY + this.getLastPosY());
            this.getComponents().forEach(component -> component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY()));
        }
        if (this.getPosX() < 0.0f) {
            this.setPosX(0.0f);
            this.getComponents().forEach(component -> component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY()));
        }
        if (this.getPosX() + this.getWidth() > (float)scaledResolution.getScaledWidth()) {
            this.setPosX((float)scaledResolution.getScaledWidth() - this.getWidth());
            this.getComponents().forEach(component -> component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY()));
        }
        if (this.getPosY() < 0.0f) {
            this.setPosY(0.0f);
            this.getComponents().forEach(component -> component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY()));
        }
        if (this.getPosY() + this.getHeight() > (float)scaledResolution.getScaledHeight()) {
            this.setPosY((float)scaledResolution.getScaledHeight() - this.getHeight());
            this.getComponents().forEach(component -> component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY()));
        }
    }

    public void keyTyped(char character, int keyCode) {
        if (this.isExtended()) {
            this.getComponents().forEach(component -> component.keyTyped(character, keyCode));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight());
        switch (mouseButton) {
            case 0: {
                if (!hovered) break;
                this.setDragging(true);
                this.setLastPosX(this.getPosX() - (float)mouseX);
                this.setLastPosY(this.getPosY() - (float)mouseY);
                break;
            }
            case 1: {
                if (!hovered) break;
                this.setExtended(!this.isExtended());
                break;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isDragging()) {
            this.setDragging(false);
        }
        if (this.isExtended()) {
            this.getComponents().forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
        }
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }

    public String getLabel() {
        return this.label;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getPosX() {
        return this.posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getLastPosX() {
        return this.lastPosX;
    }

    public void setLastPosX(float lastPosX) {
        this.lastPosX = lastPosX;
    }

    public float getLastPosY() {
        return this.lastPosY;
    }

    public void setLastPosY(float lastPosY) {
        this.lastPosY = lastPosY;
    }

    public boolean isExtended() {
        return this.extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public int getScrollY() {
        return this.scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
