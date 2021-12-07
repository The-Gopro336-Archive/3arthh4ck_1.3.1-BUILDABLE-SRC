package me.earth.earthhack.impl.util.misc;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.AbstractGuiElement;

public class GuiUtil {
    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isHovered(AbstractGuiElement element, int mouseX, int mouseY) {
        return GuiUtil.isHovered(element.getX(), element.getY(), element.getWidth(), element.getHeight(), (float)mouseX, (float)mouseY);
    }

    public static boolean isHovered(HudElement element, int mouseX, int mouseY) {
        return GuiUtil.isHovered(element.getX(), element.getY(), element.getWidth(), element.getHeight(), (float)mouseX, (float)mouseY);
    }

    public static float reCheckSliderRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double roundSliderForConfig(double val) {
        return Double.parseDouble(String.format("%.2f", val));
    }

    public static String roundSlider(float f) {
        return String.format("%.2f", Float.valueOf(f));
    }

    public static float roundSliderStep(float input, float step) {
        return (float)Math.round(input / step) * step;
    }

    public static boolean isHoveredOnEdge(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        return GuiUtil.isHovered(x, y, width, height, mouseX, mouseY) && (mouseX < x + edge || mouseX > x + width - edge || mouseY < y + edge || mouseY > y + height - edge);
    }

    public static Edge getHoveredEdge(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        if (GuiUtil.isHovered(x, y, edge, edge, mouseX, mouseY)) {
            return Edge.TOP_LEFT;
        }
        if (GuiUtil.isHovered(x, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_LEFT;
        }
        if (GuiUtil.isHovered(x + width - edge, y, edge, edge, mouseX, mouseY)) {
            return Edge.TOP_RIGHT;
        }
        if (GuiUtil.isHovered(x + width - edge, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_RIGHT;
        }
        if (GuiUtil.isHovered(x, y, edge, height, mouseX, mouseY)) {
            return Edge.LEFT;
        }
        if (GuiUtil.isHovered(x + width - edge, y, edge, height, mouseX, mouseY)) {
            return Edge.RIGHT;
        }
        if (GuiUtil.isHovered(x, y + edge, width, edge, mouseX, mouseY)) {
            return Edge.TOP;
        }
        if (GuiUtil.isHovered(x, y + height - edge, width, edge, mouseX, mouseY)) {
            return Edge.BOTTOM;
        }
        return null;
    }

    public static Edge getHoveredEdgeNoTop(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        if (!GuiUtil.isHovered(x, y, edge, edge, mouseX, mouseY)) {
            if (GuiUtil.isHovered(x, y + height - edge, edge, edge, mouseX, mouseY)) {
                return Edge.BOTTOM_LEFT;
            }
            if (!GuiUtil.isHovered(x + width - edge, y, edge, edge, mouseX, mouseY)) {
                if (GuiUtil.isHovered(x + width - edge, y + height - edge, edge, edge, mouseX, mouseY)) {
                    return Edge.BOTTOM_RIGHT;
                }
                if (GuiUtil.isHovered(x, y, edge, height, mouseX, mouseY)) {
                    return Edge.LEFT;
                }
                if (GuiUtil.isHovered(x + width - edge, y, edge, height, mouseX, mouseY)) {
                    return Edge.RIGHT;
                }
                if (!GuiUtil.isHovered(x, y + edge, width, edge, mouseX, mouseY) && GuiUtil.isHovered(x, y + height - edge, width, edge, mouseX, mouseY)) {
                    return Edge.BOTTOM;
                }
            }
        }
        return null;
    }

    public static Edge getHoveredEdgeNoTop(AbstractGuiElement element, int mouseX, int mouseY, int edge) {
        return GuiUtil.getHoveredEdgeNoTop((int)element.getX(), (int)element.getY(), (int)element.getWidth(), (int)element.getHeight(), mouseX, mouseY, edge);
    }

    public static Edge getHoveredEdge(AbstractGuiElement element, int mouseX, int mouseY, int edge) {
        return GuiUtil.getHoveredEdge((int)element.getX(), (int)element.getY(), (int)element.getWidth(), (int)element.getHeight(), mouseX, mouseY, edge);
    }

    public static Edge getHoveredEdge(HudElement element, int mouseX, int mouseY, int edge) {
        return GuiUtil.getHoveredEdge((int)element.getX(), (int)element.getY(), (int)element.getWidth(), (int)element.getHeight(), mouseX, mouseY, edge);
    }

    public static enum Edge {
        RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT;

    }
}
