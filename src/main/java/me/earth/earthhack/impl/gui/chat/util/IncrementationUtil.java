/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.impl.gui.chat.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import org.lwjgl.input.Keyboard;

public class IncrementationUtil {
    public static final int MAX = 157;
    public static final int FASTER = 29;
    public static final int FAST = 56;

    public static String crD(String d, String min, String max, boolean de) {
        BigDecimal incr;
        if (Keyboard.isKeyDown((int)157)) {
            if (de) {
                return min;
            }
            return max;
        }
        BigDecimal v = new BigDecimal(d);
        BigDecimal n = new BigDecimal(min);
        BigDecimal m = new BigDecimal(max);
        if (Keyboard.isKeyDown((int)29)) {
            if (Keyboard.isKeyDown((int)56)) {
                BigDecimal diff = m.subtract(n);
                incr = diff.divide(new BigDecimal(de ? "-10" : "10"), RoundingMode.FLOOR);
            } else {
                incr = new BigDecimal(de ? "-1.0" : "1.0");
            }
        } else {
            incr = Keyboard.isKeyDown((int)56) ? new BigDecimal(de ? "-10.0" : "10.0") : new BigDecimal(de ? "-0.1" : "0.1");
        }
        return MathUtil.clamp(v.add(incr), n, m).toString();
    }

    public static double crF(double d, double min, double max, boolean de) {
        BigDecimal incr;
        if (Keyboard.isKeyDown((int)157)) {
            if (de) {
                return min;
            }
            return max;
        }
        BigDecimal v = new BigDecimal(d);
        BigDecimal n = new BigDecimal(min);
        BigDecimal m = new BigDecimal(max);
        if (Keyboard.isKeyDown((int)29)) {
            if (Keyboard.isKeyDown((int)56)) {
                BigDecimal diff = m.subtract(n);
                incr = diff.divide(new BigDecimal(de ? "-10" : "10"), RoundingMode.FLOOR);
            } else {
                incr = new BigDecimal(de ? "-1.0" : "1.0");
            }
        } else {
            incr = Keyboard.isKeyDown((int)56) ? new BigDecimal(de ? "-10.0" : "10.0") : new BigDecimal(de ? "-0.1" : "0.1");
        }
        return MathUtil.clamp(v.add(incr), n, m).doubleValue();
    }

    public static long crL(long l, long min, long max, boolean de) {
        long incr;
        if (Keyboard.isKeyDown((int)157)) {
            if (de) {
                return min;
            }
            return max;
        }
        if (Keyboard.isKeyDown((int)29)) {
            long diff = max - min;
            incr = Keyboard.isKeyDown((int)56) ? diff / 10L : diff / 5L;
        } else {
            incr = Keyboard.isKeyDown((int)56) ? 10L : 1L;
        }
        return MathUtil.clamp(l + (de ? -incr : incr), min, max);
    }
}

