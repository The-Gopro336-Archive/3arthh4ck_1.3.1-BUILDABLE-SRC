/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HelperUtil
implements Globals {
    public static BreakValidity isValid(AutoCrystal module, Entity crystal) {
        block8: {
            block7: {
                if (module.existed.getValue() != 0) {
                    double d = System.currentTimeMillis() - ((IEntity)crystal).getTimeStamp();
                    double d2 = module.pingExisted.getValue() != false ? (double)ServerUtil.getPingNoPingSpoof() / 2.0 : 0.0;
                    if (d + d2 < (double)module.existed.getValue().intValue()) {
                        return BreakValidity.INVALID;
                    }
                }
                if (RotationUtil.getRotationPlayer().getDistanceSq(crystal) >= (double)MathUtil.square(module.breakRange.getValue().floatValue())) {
                    return BreakValidity.INVALID;
                }
                if (RotationUtil.getRotationPlayer().getDistanceSq(crystal) >= (double)MathUtil.square(module.breakTrace.getValue().floatValue()) && !RayTraceUtil.canBeSeen(new Vec3d(crystal.posX, crystal.posY + 1.7, crystal.posZ), (Entity)RotationUtil.getRotationPlayer())) {
                    return BreakValidity.INVALID;
                }
                if (module.rotate.getValue().noRotate(ACRotate.Break)) break block7;
                if (!RotationUtil.isLegit(crystal, crystal) || !module.positionHistoryHelper.arePreviousRotationsLegit(crystal, module.rotationTicks.getValue(), true)) break block8;
            }
            return BreakValidity.VALID;
        }
        return BreakValidity.ROTATIONS;
    }

    public static void simulateExplosion(AutoCrystal module, double x, double y, double z) {
        List<Entity> entities = Managers.ENTITIES.getEntities();
        if (entities == null) {
            return;
        }
        for (Entity entity : entities) {
            if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(x, y, z) < 144.0)) continue;
            if (module.pseudoSetDead.getValue().booleanValue()) {
                ((IEntity)entity).setPseudoDead(true);
                continue;
            }
            Managers.SET_DEAD.setDead(entity);
        }
    }

    public static boolean validChange(BlockPos pos, List<EntityPlayer> players) {
        for (EntityPlayer player : players) {
            if (player == null || player.equals((Object)HelperUtil.mc.player) || player.equals((Object)RotationUtil.getRotationPlayer()) || EntityUtil.isDead((Entity)player) || Managers.FRIENDS.contains(player) || !(player.getDistanceSqToCenter(pos) <= 4.0) || !(player.posY >= (double)pos.getY())) continue;
            return true;
        }
        return false;
    }

    public static boolean valid(Entity entity, double range, double trace) {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        double d = entity.getDistanceSq((Entity)player);
        if (d >= MathUtil.square(range)) {
            return false;
        }
        if (d >= trace) {
            return RayTraceUtil.canBeSeen(entity, (EntityLivingBase)player);
        }
        return true;
    }
}

