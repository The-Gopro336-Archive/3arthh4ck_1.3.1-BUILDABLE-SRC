/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.render;

import java.awt.Color;

public class ColorHelper {
    public static float getFactor(float red, float green, float blue) {
        if (blue < 0.0f) {
            blue += 1.0f;
        }
        if (blue > 1.0f) {
            blue -= 1.0f;
        }
        if (6.0f * blue < 1.0f) {
            return red + (green - red) * 6.0f * blue;
        }
        if (2.0f * blue < 1.0f) {
            return green;
        }
        return 3.0f * blue < 2.0f ? red + (green - red) * 6.0f * (0.6666667f - blue) : red;
    }

    public static Color toColor(float red, float green, float blue, float alpha) {
        if (!(green < 0.0f) && !(green > 100.0f)) {
            if (!(blue < 0.0f) && !(blue > 100.0f)) {
                if (!(alpha < 0.0f) && !(alpha > 1.0f)) {
                    red = red % 360.0f / 360.0f;
                    float blueOff = (double)(blue /= 100.0f) < 0.0 ? blue * (1.0f + green) : blue + (green /= 100.0f) - green * blue;
                    green = 2.0f * blue - blueOff;
                    blue = Math.max(0.0f, ColorHelper.getFactor(green, blueOff, red + 0.33333334f));
                    float max = Math.max(0.0f, ColorHelper.getFactor(green, blueOff, red));
                    green = Math.max(0.0f, ColorHelper.getFactor(green, blueOff, red - 0.33333334f));
                    blue = Math.min(blue, 1.0f);
                    max = Math.min(max, 1.0f);
                    green = Math.min(green, 1.0f);
                    return new Color(blue, max, green, alpha);
                }
                throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
            }
            throw new IllegalArgumentException("Color parameter outside of expected range - Lightness");
        }
        throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
    }
}

