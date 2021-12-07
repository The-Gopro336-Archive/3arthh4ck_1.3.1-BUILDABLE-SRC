package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.Orientation;
import me.earth.earthhack.impl.gui.hud.SnapPoint;
import me.earth.earthhack.impl.managers.Managers;

public class ElementSnapPoint
extends SnapPoint {
    private final HudElement element;

    public ElementSnapPoint(HudElement element, Orientation orientation) {
        super(element.getX(), element.getY(), element.getWidth(), orientation);
        switch (orientation) {
            case TOP: {
                this.x = element.getX();
                this.y = element.getY();
                this.length = element.getWidth();
                break;
            }
            case BOTTOM: {
                this.x = element.getX();
                this.y = element.getY() + element.getHeight();
                this.length = element.getWidth();
                break;
            }
            case LEFT: {
                this.x = element.getX();
                this.y = element.getY();
                this.length = element.getHeight();
                break;
            }
            case RIGHT: {
                this.x = element.getX() + element.getWidth();
                this.y = element.getY();
                this.length = element.getHeight();
            }
        }
        this.element = element;
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        for (HudElement hudElement : Managers.ELEMENTS.getRegistered()) {
            if (hudElement == this.element) continue;
            if (this.orientation == Orientation.LEFT && hudElement.getX() <= this.x + 4.0f && hudElement.getX() >= this.x - 4.0f) {
                if (hudElement.isDragging() || this.x == hudElement.getX()) continue;
                hudElement.setX(this.x);
                continue;
            }
            if (this.orientation == Orientation.RIGHT && hudElement.getX() + hudElement.getWidth() <= this.x + 4.0f && hudElement.getX() + hudElement.getWidth() >= this.x - 4.0f) {
                if (hudElement.isDragging() || this.x == hudElement.getX() + hudElement.getWidth()) continue;
                hudElement.setX(this.x - hudElement.getWidth());
                continue;
            }
            if (this.orientation == Orientation.TOP && hudElement.getY() <= this.y + 4.0f && hudElement.getY() >= this.y - 4.0f) {
                if (hudElement.isDragging() || this.y == hudElement.getY()) continue;
                hudElement.setY(this.y);
                continue;
            }
            if (this.orientation != Orientation.BOTTOM || !(hudElement.getY() + hudElement.getHeight() <= this.y + 4.0f) || !(hudElement.getY() + hudElement.getHeight() >= this.y - 4.0f) || hudElement.isDragging() || this.y == hudElement.getY() + hudElement.getHeight()) continue;
            hudElement.setY(this.y - hudElement.getHeight());
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
    }

    public HudElement getElement() {
        return this.element;
    }
}
