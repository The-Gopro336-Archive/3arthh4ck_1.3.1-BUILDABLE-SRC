package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.ColorFunctions;

public class ColorFunctions$HSLIncreaseDecrease
implements ColorFunctions.ColorFunction {
    public final int hslIndex;
    public final boolean increase;
    public final float amount;
    public final boolean relative;
    public final boolean autoInverse;

    public ColorFunctions$HSLIncreaseDecrease(int hslIndex, boolean increase, float amount, boolean relative, boolean autoInverse) {
        this.hslIndex = hslIndex;
        this.increase = increase;
        this.amount = amount;
        this.relative = relative;
        this.autoInverse = autoInverse;
    }

    @Override
    public void apply(float[] hsla) {
        float amount2;
        float f = amount2 = this.increase ? this.amount : -this.amount;
        if (this.hslIndex == 0) {
            hsla[0] = (hsla[0] + amount2) % 360.0f;
            return;
        }
        amount2 = this.autoInverse && this.shouldInverse(hsla) ? -amount2 : amount2;
        hsla[this.hslIndex] = ColorFunctions.clamp(this.relative ? hsla[this.hslIndex] * ((100.0f + amount2) / 100.0f) : hsla[this.hslIndex] + amount2);
    }

    protected boolean shouldInverse(float[] hsla) {
        return this.increase ? hsla[this.hslIndex] > 65.0f : hsla[this.hslIndex] < 35.0f;
    }

    public String toString() {
        String name;
        switch (this.hslIndex) {
            case 0: {
                name = "spin";
                break;
            }
            case 1: {
                name = this.increase ? "saturate" : "desaturate";
                break;
            }
            case 2: {
                name = this.increase ? "lighten" : "darken";
                break;
            }
            case 3: {
                name = this.increase ? "fadein" : "fadeout";
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return String.format("%s(%.0f%%%s%s)", name, Float.valueOf(this.amount), this.relative ? " relative" : "", this.autoInverse ? " autoInverse" : "");
    }
}
