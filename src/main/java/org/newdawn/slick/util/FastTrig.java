/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.util;

public class FastTrig {
    private static double reduceSinAngle(double radians) {
        double orig = radians;
        if (Math.abs(radians %= Math.PI * 2) > Math.PI) {
            radians -= Math.PI * 2;
        }
        if (Math.abs(radians) > 1.5707963267948966) {
            radians = Math.PI - radians;
        }
        return radians;
    }

    public static double sin(double radians) {
        if (Math.abs(radians = FastTrig.reduceSinAngle(radians)) <= 0.7853981633974483) {
            return Math.sin(radians);
        }
        return Math.cos(1.5707963267948966 - radians);
    }

    public static double cos(double radians) {
        return FastTrig.sin(radians + 1.5707963267948966);
    }
}

