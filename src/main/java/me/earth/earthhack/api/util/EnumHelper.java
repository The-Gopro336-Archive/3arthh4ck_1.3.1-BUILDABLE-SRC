/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.util;

public class EnumHelper {
    public static Enum<?> next(Enum<?> entry) {
        Enum[] array = (Enum[])entry.getDeclaringClass().getEnumConstants();
        return array.length - 1 == entry.ordinal() ? array[0] : array[entry.ordinal() + 1];
    }

    public static Enum<?> previous(Enum<?> entry) {
        Enum[] array = (Enum[])entry.getDeclaringClass().getEnumConstants();
        return entry.ordinal() - 1 < 0 ? array[array.length - 1] : array[entry.ordinal() - 1];
    }

    public static Enum<?> fromString(Enum<?> initial, String name) {
        Object e = EnumHelper.fromString(initial.getDeclaringClass(), name);
        if (e != null) {
            return e;
        }
        return initial;
    }

    public static <T extends Enum<?>> T fromString(Class<T> type, String name) {
        for (Enum constant : (Enum[])type.getEnumConstants()) {
            if (!constant.name().equalsIgnoreCase(name)) continue;
            return (T)constant;
        }
        return null;
    }

    public static Enum<?> getEnumStartingWith(String prefix, Class<? extends Enum<?>> type) {
        Enum<?>[] array;
        if (prefix == null) {
            return null;
        }
        prefix = prefix.toLowerCase();
        for (Enum<?> entry : array = type.getEnumConstants()) {
            if (!entry.name().toLowerCase().startsWith(prefix)) continue;
            return entry;
        }
        return null;
    }
}

