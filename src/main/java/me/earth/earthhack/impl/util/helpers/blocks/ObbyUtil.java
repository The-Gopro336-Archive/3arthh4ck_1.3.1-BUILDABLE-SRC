/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.network.play.client.CPacketUseEntity
 */
package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;

public class ObbyUtil {
    public static boolean place(ObbyModule module, Pathable path) {
        if (!path.isValid()) {
            return false;
        }
        Entity target = null;
        boolean crystalFound = false;
        float maxDamage = Float.MAX_VALUE;
        for (BlockingEntity entity : path.getBlockingEntities()) {
            if (module.attack.getValue().booleanValue() && entity.getEntity() instanceof EntityEnderCrystal) {
                crystalFound = true;
                float damage = DamageUtil.calculate(entity.getEntity(), (EntityLivingBase)module.getPlayer());
                if (!(damage < maxDamage) || !module.pop.getValue().shouldPop(damage, module.popTime.getValue())) continue;
                maxDamage = damage;
                target = entity.getEntity();
                continue;
            }
            return false;
        }
        if (target != null) {
            module.attacking = new CPacketUseEntity(target);
        } else if (crystalFound && module.blockingType.getValue() != BlockingType.Crystals) {
            return false;
        }
        for (Ray ray : path.getPath()) {
            module.placeBlock(ray.getPos(), ray.getFacing(), ray.getRotations(), ray.getResult().hitVec);
            if (module.blocksPlaced < (Integer)module.blocks.getValue() && module.rotate.getValue() != Rotate.Normal) continue;
            return true;
        }
        return true;
    }
}

