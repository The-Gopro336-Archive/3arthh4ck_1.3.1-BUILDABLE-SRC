package me.earth.earthhack.impl.gui.hud;

import java.awt.Color;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.Orientation;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;

public class SnapPoint {
    protected Orientation orientation;
    protected float x;
    protected float y;
    protected float length;

    public SnapPoint(float x, float y, float length, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.orientation = orientation;
    }

    public void update(int mouseX, int mouseY, float partialTicks) {
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (this.orientation == Orientation.LEFT && element.getX() <= this.x + 4.0f && element.getX() >= this.x - 4.0f) {
                if (element.isDragging() || this.x == element.getX()) continue;
                element.setX(this.x);
                continue;
            }
            if (this.orientation == Orientation.RIGHT && element.getX() + element.getWidth() <= this.x + 4.0f && element.getX() + element.getWidth() >= this.x - 4.0f) {
                if (element.isDragging() || this.x == element.getX() + element.getWidth()) continue;
                element.setX(this.x - element.getWidth());
                continue;
            }
            if (this.orientation == Orientation.TOP && element.getY() <= this.y + 4.0f && element.getY() >= this.y - 4.0f) {
                if (element.isDragging() || this.y == element.getY()) continue;
                element.setY(this.y);
                continue;
            }
            if (this.orientation != Orientation.BOTTOM || !(element.getY() + element.getHeight() <= this.y + 4.0f) || !(element.getY() + element.getHeight() >= this.y - 4.0f) || element.isDragging() || this.y == element.getY() + element.getHeight()) continue;
            element.setY(this.y - element.getHeight());
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        switch (this.orientation) {
            case TOP: 
            case BOTTOM: {
                Render2DUtil.drawLine(this.x, this.y, this.x + this.length, this.y, 1.0f, Color.WHITE.getRGB());
                break;
            }
            case RIGHT: 
            case LEFT: {
                Render2DUtil.drawLine(this.x, this.y, this.x, this.y + this.length, 1.0f, Color.WHITE.getRGB());
            }
        }
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getLength() {
        return this.length;
    }

    public void setLength(float length) {
        this.length = length;
    }
}
