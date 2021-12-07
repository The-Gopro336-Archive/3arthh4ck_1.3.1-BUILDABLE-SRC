/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class TimeStamp {
    private final long timeStamp = System.currentTimeMillis();
    private boolean valid = true;

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

