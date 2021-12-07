/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.api.util.bind;

import org.lwjgl.input.Keyboard;

public class Bind {
    private final int key;

    private Bind(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public int hashCode() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (o instanceof Bind) {
            return ((Bind)o).key == this.key;
        }
        return false;
    }

    public String toString() {
        return this.key < 0 ? "NONE" : Keyboard.getKeyName((int)this.key);
    }

    public static Bind none() {
        return new Bind(-1);
    }

    public static Bind fromKey(int key) {
        return new Bind(key);
    }

    public static Bind fromString(String string) {
        if ((string = string.toUpperCase()).equals("NONE")) {
            return Bind.none();
        }
        return new Bind(Keyboard.getKeyIndex((String)string));
    }
}

