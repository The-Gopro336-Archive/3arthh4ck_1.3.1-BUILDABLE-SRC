/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;

public class CrystalTimeStamp
extends TimeStamp {
    private final float damage;

    public CrystalTimeStamp(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }
}

