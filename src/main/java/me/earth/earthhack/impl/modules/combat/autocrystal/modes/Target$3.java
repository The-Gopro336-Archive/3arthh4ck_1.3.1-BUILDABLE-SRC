package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import java.util.List;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import net.minecraft.entity.player.EntityPlayer;

final class Target$3
extends Target {
    @Override
    public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
        EntityPlayer enemy = Target$3.getByAngle(enemies, maxRange);
        if (enemy == null) {
            return Target$3.getByAngle(players, maxRange);
        }
        return enemy;
    }
}
