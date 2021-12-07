package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

public enum Target implements Globals
{
    Closest{

        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
            return EntityUtil.getClosestEnemy(1.mc.player.posX, 1.mc.player.posY, 1.mc.player.posZ, maxRange, enemies, players);
        }
    }
    ,
    FOV{

        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
            EntityPlayer enemy = 2.getByFov(enemies, maxRange);
            if (enemy == null) {
                return 2.getByFov(players, maxRange);
            }
            return enemy;
        }
    }
    ,
    Angle{

        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
            EntityPlayer enemy = 3.getByAngle(enemies, maxRange);
            if (enemy == null) {
                return 3.getByAngle(players, maxRange);
            }
            return enemy;
        }
    }
    ,
    Damage{

        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, List<EntityPlayer> enemies, double maxRange) {
            return null;
        }
    };

    public static final String DESCRIPTION = "-Closest, will target the closest Enemy.\n-FOV, will target the player you are looking at (by Angle).\n-Angle, similar to FOV but will also target players outside your FOV.\n-Damage, Calculates Damages for all Players in Range and takes the best one (intensive).";

    public abstract EntityPlayer getTarget(List<EntityPlayer> var1, List<EntityPlayer> var2, double var3);

    public static EntityPlayer getByFov(List<EntityPlayer> players, double maxRange) {
        EntityPlayer closest = null;
        double closestAngle = 360.0;
        for (EntityPlayer player : players) {
            double angle;
            if (!EntityUtil.isValid(player, maxRange) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle) || !(angle < (double)(Target.mc.gameSettings.fovSetting / 2.0f))) continue;
            closest = player;
            closestAngle = angle;
        }
        return closest;
    }

    public static EntityPlayer getByAngle(List<EntityPlayer> players, double maxRange) {
        EntityPlayer closest = null;
        double closestAngle = 360.0;
        for (EntityPlayer player : players) {
            double angle;
            if (!EntityUtil.isValid(player, maxRange) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle) || !(angle < (double)(Target.mc.gameSettings.fovSetting / 2.0f))) continue;
            closest = player;
            closestAngle = angle;
        }
        return closest;
    }
}
