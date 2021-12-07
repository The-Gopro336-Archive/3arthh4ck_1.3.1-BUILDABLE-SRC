/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.misc.collections;

public class ArrayUtil {
    public static boolean contains(char ch, char[] array) {
        for (char c : array) {
            if (ch != c) continue;
            return true;
        }
        return false;
    }
}

