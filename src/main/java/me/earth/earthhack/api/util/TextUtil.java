package me.earth.earthhack.api.util;

public class TextUtil {
    public static boolean startsWith(String string, String prefix) {
        if (string == null || prefix == null) {
            return false;
        }
        return string.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public static String substring(String string, int beginIndex) {
        if (string != null && beginIndex <= string.length()) {
            return string.substring(Math.max(beginIndex, 0));
        }
        return "";
    }

    public static String substring(String string, int beginIndex, int endIndex) {
        if (string != null && beginIndex <= string.length() && endIndex > 0 && endIndex >= beginIndex) {
            return string.substring(Math.max(0, beginIndex), Math.min(endIndex, string.length()));
        }
        return "";
    }

    public static String get32BitString(int value) {
        StringBuilder r = new StringBuilder(Integer.toHexString(value));
        while (r.length() < 8) {
            r.insert(0, 0);
        }
        return r.toString().toUpperCase();
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
