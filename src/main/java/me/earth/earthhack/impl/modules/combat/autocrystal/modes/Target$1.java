package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import java.util.List;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

final class Target$1
extends Target {
    @Override
    public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
        return EntityUtil.getClosestEnemy(Target$1.mc.player.posX, Target$1.mc.player.posY, Target$1.mc.player.posZ, maxRange, enemies, players);
    }
}
