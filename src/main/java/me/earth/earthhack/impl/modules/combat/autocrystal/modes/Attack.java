/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import java.util.function.Supplier;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

public enum Attack {
    Always(() -> true, () -> true),
    Crystal(() -> InventoryUtil.isHolding(Items.END_CRYSTAL), () -> InventoryUtil.isHolding(Items.END_CRYSTAL)),
    Calc(() -> true, () -> InventoryUtil.isHolding(Items.END_CRYSTAL));

    Supplier<Boolean> shouldCalc;
    Supplier<Boolean> shouldAttack;

    private Attack(Supplier<Boolean> shouldCalc, Supplier<Boolean> shouldAttack) {
        this.shouldAttack = shouldAttack;
        this.shouldCalc = shouldCalc;
    }

    public boolean shouldCalc() {
        return this.shouldCalc.get();
    }

    public boolean shouldAttack() {
        return this.shouldAttack.get();
    }
}

