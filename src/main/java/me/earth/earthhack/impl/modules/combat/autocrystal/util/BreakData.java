/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Collection;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import net.minecraft.entity.Entity;

public class BreakData<T extends CrystalData> {
    private final Collection<T> data;
    private float fallBackDmg = Float.MAX_VALUE;
    private Entity antiTotem;
    private Entity fallBack;

    public BreakData(Collection<T> data) {
        this.data = data;
    }

    public void register(T dataIn) {
        if (((CrystalData)dataIn).getSelfDmg() < this.fallBackDmg) {
            this.fallBack = ((CrystalData)dataIn).getCrystal();
            this.fallBackDmg = ((CrystalData)dataIn).getSelfDmg();
        }
        this.data.add(dataIn);
    }

    public float getFallBackDmg() {
        return this.fallBackDmg;
    }

    public Entity getAntiTotem() {
        return this.antiTotem;
    }

    public void setAntiTotem(Entity antiTotem) {
        this.antiTotem = antiTotem;
    }

    public Entity getFallBack() {
        return this.fallBack;
    }

    public Collection<T> getData() {
        return this.data;
    }
}

