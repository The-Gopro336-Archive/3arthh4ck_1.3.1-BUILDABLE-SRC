package me.earth.earthhack.impl.util.minecraft.entity;

import java.util.List;
import java.util.function.Predicate;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityUtil
implements Globals {
    public static boolean isDead(Entity entity) {
        return entity.isDead || ((IEntity)((Object)entity)).isPseudoDead() || entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0f;
    }

    public static float getHealth(EntityLivingBase base) {
        return base.getHealth() + base.getAbsorptionAmount();
    }

    public static float getHealth(EntityLivingBase base, boolean absorption) {
        if (absorption) {
            return base.getHealth() + base.getAbsorptionAmount();
        }
        return base.getHealth();
    }

    public static EntityPlayer getClosestEnemy() {
        return EntityUtil.getClosestEnemy(EntityUtil.mc.world.playerEntities);
    }

    public static EntityPlayer getClosestEnemy(List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(EntityUtil.mc.player.getPositionVector(), list);
    }

    public static EntityPlayer getClosestEnemy(BlockPos pos, List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(pos.getX(), pos.getY(), pos.getZ(), list);
    }

    public static EntityPlayer getClosestEnemy(Vec3d vec3d, List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(vec3d.x, vec3d.y, vec3d.z, list);
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, List<EntityPlayer> players) {
        EntityPlayer closest = null;
        double distance = 3.4028234663852886E38;
        for (EntityPlayer player : players) {
            double dist;
            if (player == null || EntityUtil.isDead(player) || player.equals(EntityUtil.mc.player) || Managers.FRIENDS.contains(player) || !((dist = player.getDistanceSq(x, y, z)) < distance)) continue;
            closest = player;
            distance = dist;
        }
        return closest;
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, double maxRange, List<EntityPlayer> players) {
        Predicate[] predicateArray = new Predicate[1];
        predicateArray[0] = Managers.ENEMIES::contains;
        List<List<EntityPlayer>> split = CollectionUtil.split(players, predicateArray);
        return EntityUtil.getClosestEnemy(x, y, z, maxRange, split.get(0), split.get(1));
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, double maxRange, List<EntityPlayer> enemies, List<EntityPlayer> players) {
        EntityPlayer closestEnemied = EntityUtil.getClosestEnemy(x, y, z, enemies);
        if (closestEnemied != null && closestEnemied.getDistanceSq(x, y, z) < MathUtil.square(maxRange)) {
            return closestEnemied;
        }
        return EntityUtil.getClosestEnemy(x, y, z, players);
    }

    public static boolean isValid(Entity player, double range) {
        return player != null && !EntityUtil.isDead(player) && EntityUtil.mc.player.getDistanceSq(player) <= MathUtil.square(range) && !Managers.FRIENDS.contains(player);
    }
}
