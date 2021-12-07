package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import me.earth.earthhack.impl.modules.combat.autoarmor.util.DamageStack;
import net.minecraft.item.ItemStack;

public class LevelStack
extends DamageStack {
    private final int level;

    public LevelStack(ItemStack stack, float damage, int slot, int level) {
        super(stack, damage, slot);
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isBetter(float damage, float min, int level, boolean prio) {
        if (level > this.level) {
            return false;
        }
        if (level < this.level) {
            return true;
        }
        if (prio) {
            return !(damage > min) || !(damage < this.getDamage());
        }
        return !(damage > this.getDamage());
    }
}
