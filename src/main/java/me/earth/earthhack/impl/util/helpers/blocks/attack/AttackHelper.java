package me.earth.earthhack.impl.util.helpers.blocks.attack;

import java.util.List;
import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackingModule;
import net.minecraft.entity.Entity;

public interface AttackHelper {
    public boolean attackAny(List<Entity> var1, AttackingModule var2);
}
