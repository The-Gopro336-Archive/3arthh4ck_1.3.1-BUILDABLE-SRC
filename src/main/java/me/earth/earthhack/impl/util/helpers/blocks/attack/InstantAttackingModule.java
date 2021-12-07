package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackingModule;
import me.earth.earthhack.impl.util.math.Passable;
import net.minecraft.entity.item.EntityEnderCrystal;

public interface InstantAttackingModule
extends AttackingModule {
    default public void postAttack(EntityEnderCrystal entity) {
    }

    public boolean shouldAttack(EntityEnderCrystal var1);

    public Passable getTimer();

    public int getBreakDelay();

    public int getCooldown();
}
