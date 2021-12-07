/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;

public class WeaknessHelper {
    private final Setting<AntiWeakness> antiWeakness;
    private final Setting<Integer> cooldown;
    private boolean weaknessed;

    public WeaknessHelper(Setting<AntiWeakness> antiWeakness, Setting<Integer> cooldown) {
        this.antiWeakness = antiWeakness;
        this.cooldown = cooldown;
    }

    public void updateWeakness() {
        this.weaknessed = !DamageUtil.canBreakWeakness(true);
    }

    public boolean isWeaknessed() {
        return this.weaknessed;
    }

    public boolean canSwitch() {
        return this.antiWeakness.getValue() == AntiWeakness.Switch && this.cooldown.getValue() == 0 && this.weaknessed;
    }
}

