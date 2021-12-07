/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.render;

import java.awt.Color;

public class ColorUtil {
    public static int toARGB(int r, int g, int b) {
        return ColorUtil.toARGB(r, g, b, 255);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toARGB(Color color) {
        return ColorUtil.toARGB(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static float[] toArray(Color color) {
        return new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f};
    }

    public static float[] toArray(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static int staticRainbow(float offset, Color color) {
        double timer = (double)System.currentTimeMillis() % 1750.0 / 850.0;
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = (float)((double)hsb[2] * Math.abs(((double)offset + timer) % 1.0 - (double)0.55f) + (double)0.45f);
        return Color.HSBtoRGB(hsb[0], hsb[1], brightness);
    }

    public static Color getRainbow(int speed, int offset, float s, float brightness) {
        float hue = (System.currentTimeMillis() + (long)offset) % (long)speed;
        return Color.getHSBColor(hue /= (float)speed, s, brightness);
    }
}

