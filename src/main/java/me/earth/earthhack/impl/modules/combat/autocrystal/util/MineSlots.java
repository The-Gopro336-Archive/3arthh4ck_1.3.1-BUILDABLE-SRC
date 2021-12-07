/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class MineSlots {
    private final int blockSlot;
    private final int toolSlot;
    private final float damage;

    public MineSlots(int blockSlot, int toolSlot, float damage) {
        this.blockSlot = blockSlot;
        this.toolSlot = toolSlot;
        this.damage = damage;
    }

    public int getBlockSlot() {
        return this.blockSlot;
    }

    public int getToolSlot() {
        return this.toolSlot;
    }

    public float getDamage() {
        return this.damage;
    }
}

