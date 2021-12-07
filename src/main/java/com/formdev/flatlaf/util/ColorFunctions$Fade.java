package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.ColorFunctions;

public class ColorFunctions$Fade
implements ColorFunctions.ColorFunction {
    public final float amount;

    public ColorFunctions$Fade(float amount) {
        this.amount = amount;
    }

    @Override
    public void apply(float[] hsla) {
        hsla[3] = ColorFunctions.clamp(this.amount);
    }

    public String toString() {
        return String.format("fade(%.0f%%)", Float.valueOf(this.amount));
    }
}
