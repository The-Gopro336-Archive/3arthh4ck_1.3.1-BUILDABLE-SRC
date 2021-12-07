/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.cleaner;

public class SlotCount {
    private int count;
    private int slot;

    public SlotCount(int count, int slot) {
        this.count = count;
        this.slot = slot;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}

