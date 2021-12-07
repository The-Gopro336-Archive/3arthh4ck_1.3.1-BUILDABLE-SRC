/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class AntiTotemHelper {
    private final Setting<Float> health;
    private EntityPlayer target;
    private BlockPos targetPos;

    public AntiTotemHelper(Setting<Float> health) {
        this.health = health;
    }

    public boolean isDoublePoppable(EntityPlayer player) {
        return Managers.COMBAT.lastPop((Entity)player) > 500L && EntityUtil.getHealth((EntityLivingBase)player) <= this.health.getValue().floatValue();
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }
}

