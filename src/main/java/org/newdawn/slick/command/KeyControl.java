/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.command;

import org.newdawn.slick.command.Control;

public class KeyControl
implements Control {
    private int keycode;

    public KeyControl(int keycode) {
        this.keycode = keycode;
    }

    public boolean equals(Object o) {
        if (o instanceof KeyControl) {
            return ((KeyControl)o).keycode == this.keycode;
        }
        return false;
    }

    public int hashCode() {
        return this.keycode;
    }
}

