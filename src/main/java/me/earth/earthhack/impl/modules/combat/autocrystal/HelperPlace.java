/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.List;
import java.util.Set;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.AntiTotemData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.ForcePosition;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PlaceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class HelperPlace
implements Globals {
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD = Caches.getSetting(Safety.class, NumberSetting.class, "MaxDamage", Float.valueOf(4.0f));
    private final AutoCrystal module;

    public HelperPlace(AutoCrystal module) {
        this.module = module;
    }

    public PlaceData getData(List<EntityPlayer> general, List<EntityPlayer> players, List<EntityPlayer> enemies, List<EntityPlayer> friends, List<Entity> entities, float minDamage, Set<BlockPos> blackList, double maxY) {
        PlaceData data = new PlaceData(minDamage);
        EntityPlayer target = this.module.targetMode.getValue().getTarget(players, enemies, this.module.targetRange.getValue().floatValue());
        if (target == null && this.module.targetMode.getValue() != Target.Damage) {
            return data;
        }
        data.setTarget(target);
        this.evaluate(data, general, friends, entities, blackList, maxY);
        data.addAllCorrespondingData();
        return data;
    }

    private void evaluate(PlaceData data, List<EntityPlayer> players, List<EntityPlayer> friends, List<Entity> entities, Set<BlockPos> blackList, double maxY) {
        boolean obby = this.module.obsidian.getValue() != false && this.module.obbyTimer.passed(this.module.obbyDelay.getValue().intValue()) && (InventoryUtil.isHolding(Blocks.OBSIDIAN) || this.module.obbySwitch.getValue() != false && InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]) != -1);
        switch (this.module.preCalc.getValue()) {
            case Damage: {
                for (EntityPlayer player : players) {
                    this.preCalc(data, player, obby, entities, friends, blackList);
                }
            }
            case Target: {
                if (data.getTarget() == null) {
                    if (data.getData().isEmpty()) {
                        break;
                    }
                } else {
                    this.preCalc(data, data.getTarget(), obby, entities, friends, blackList);
                }
                for (PositionData positionData : data.getData()) {
                    if (!(positionData.getMaxDamage() > data.getMinDamage()) || !(positionData.getMaxDamage() > this.module.preCalcDamage.getValue().floatValue())) continue;
                    return;
                }
                break;
            }
        }
        BlockPos middle = PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer());
        int maxRadius = Sphere.getRadius(this.module.placeRange.getValue().floatValue());
        for (int i = 1; i < maxRadius; ++i) {
            this.calc(middle.add(Sphere.get(i)), data, players, friends, entities, obby, blackList, maxY);
        }
    }

    private void preCalc(PlaceData data, EntityPlayer player, boolean obby, List<Entity> entities, List<EntityPlayer> friends, Set<BlockPos> blackList) {
        BlockPos pos = PositionUtil.getPosition((Entity)player).down();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            PositionData pData = this.selfCalc(data, pos.offset(facing), entities, friends, obby, blackList);
            if (pData == null) continue;
            this.checkPlayer(data, player, pData);
        }
    }

    private PositionData selfCalc(PlaceData placeData, BlockPos pos, List<Entity> entities, List<EntityPlayer> friends, boolean obby, Set<BlockPos> blackList) {
        if (blackList.contains((Object)pos)) {
            return null;
        }
        PositionData data = PositionData.create(pos, obby, this.module.rotate.getValue() != ACRotate.None && this.module.rotate.getValue() != ACRotate.Break ? 0 : this.module.helpingBlocks.getValue(), this.module.newVer.getValue(), this.module.newVerEntities.getValue(), this.module.deathTime.getValue(), entities, this.module.lava.getValue(), this.module.water.getValue(), this.module.ignoreLavaItems.getValue());
        if (data.isBlocked() && !this.module.fallBack.getValue().booleanValue()) {
            return null;
        }
        if (data.isLiquid()) {
            if (!data.isLiquidValid() || this.module.liquidRayTrace.getValue() != false && (this.module.newVer.getValue() != false && (double)data.getPos().getY() >= RotationUtil.getRotationPlayer().posY + 2.0 || this.module.newVer.getValue() == false && (double)data.getPos().getY() >= RotationUtil.getRotationPlayer().posY + 1.0) || BlockUtil.getDistanceSq(pos.up()) >= (double)MathUtil.square(this.module.placeRange.getValue().floatValue()) || BlockUtil.getDistanceSq(pos.up(2)) >= (double)MathUtil.square(this.module.placeRange.getValue().floatValue())) {
                return null;
            }
            if (data.usesObby()) {
                if (data.isObbyValid()) {
                    placeData.getLiquidObby().put(data.getPos(), data);
                }
                return null;
            }
            placeData.getLiquid().add(data);
            return null;
        }
        if (data.usesObby()) {
            if (data.isObbyValid()) {
                placeData.getAllObbyData().put(data.getPos(), data);
            }
            return null;
        }
        if (!data.isValid()) {
            return null;
        }
        return this.validate(data, friends);
    }

    public PositionData validate(PositionData data, List<EntityPlayer> friends) {
        if (BlockUtil.getDistanceSq(data.getPos()) >= (double)MathUtil.square(this.module.placeTrace.getValue().floatValue()) && this.noPlaceTrace(data.getPos())) {
            return null;
        }
        float selfDamage = this.module.damageHelper.getDamage(data.getPos());
        if ((double)selfDamage > (double)EntityUtil.getHealth((EntityLivingBase)HelperPlace.mc.player) - 1.0) {
            if (!data.usesObby() && !data.isLiquid()) {
                Managers.SAFETY.setSafe(false);
            }
            if (!this.module.suicide.getValue().booleanValue()) {
                return null;
            }
        }
        if (selfDamage > MD.getValue().floatValue() && !data.usesObby() && !data.isLiquid()) {
            Managers.SAFETY.setSafe(false);
        }
        if (selfDamage > this.module.maxSelfPlace.getValue().floatValue() && !this.module.override.getValue().booleanValue()) {
            return null;
        }
        if (this.checkFriends(data, friends)) {
            return null;
        }
        data.setSelfDamage(selfDamage);
        return data;
    }

    private boolean noPlaceTrace(BlockPos pos) {
        if (this.module.smartTrace.getValue().booleanValue()) {
            for (EnumFacing facing : EnumFacing.values()) {
                Ray ray = RayTraceFactory.rayTrace((Entity)HelperPlace.mc.player, pos, facing, (IBlockAccess)HelperPlace.mc.world, Blocks.OBSIDIAN.getDefaultState(), this.module.traceWidth.getValue());
                if (!ray.isLegit()) continue;
                return false;
            }
            return true;
        }
        return !RayTraceUtil.raytracePlaceCheck((Entity)HelperPlace.mc.player, pos);
    }

    private void calc(BlockPos pos, PlaceData data, List<EntityPlayer> players, List<EntityPlayer> friends, List<Entity> entities, boolean obby, Set<BlockPos> blackList, double maxY) {
        if (this.placeCheck(pos, maxY) || data.getTarget() != null && data.getTarget().getDistanceSq(pos) > (double)MathUtil.square(this.module.range.getValue().floatValue())) {
            return;
        }
        PositionData positionData = this.selfCalc(data, pos, entities, friends, obby, blackList);
        if (positionData == null) {
            return;
        }
        this.calcPositionData(data, positionData, players);
    }

    public void calcPositionData(PlaceData data, PositionData positionData, List<EntityPlayer> players) {
        boolean isAntiTotem = false;
        if (data.getTarget() == null) {
            for (EntityPlayer player : players) {
                isAntiTotem = this.checkPlayer(data, player, positionData) || isAntiTotem;
            }
        } else {
            isAntiTotem = this.checkPlayer(data, data.getTarget(), positionData);
        }
        if (positionData.isForce()) {
            ForcePosition forcePosition = new ForcePosition(positionData);
            for (EntityPlayer forced : positionData.getForced()) {
                data.addForceData(forced, forcePosition);
            }
        }
        if (isAntiTotem) {
            data.addAntiTotem(new AntiTotemData(positionData));
        }
        if (positionData.getFacePlacer() != null || positionData.getMaxDamage() > data.getMinDamage()) {
            data.getData().add(positionData);
        }
    }

    private boolean placeCheck(BlockPos pos, double maxY) {
        if (pos.getY() < 0 || (double)(pos.getY() - 1) >= maxY || BlockUtil.getDistanceSq(pos) > (double)MathUtil.square(this.module.placeRange.getValue().floatValue())) {
            return true;
        }
        if (BlockUtil.getDistanceSq(pos) > (double)MathUtil.square(this.module.pbTrace.getValue().floatValue())) {
            return !RayTraceUtil.canBeSeen(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 2.7, (double)pos.getZ() + 0.5), (Entity)HelperPlace.mc.player);
        }
        return false;
    }

    private boolean checkFriends(PositionData data, List<EntityPlayer> friends) {
        if (!this.module.antiFriendPop.getValue().shouldCalc(AntiFriendPop.Place)) {
            return false;
        }
        for (EntityPlayer friend : friends) {
            if (friend == null || EntityUtil.isDead((Entity)friend) || !(this.module.damageHelper.getDamage(data.getPos(), (EntityLivingBase)friend) > EntityUtil.getHealth((EntityLivingBase)friend) - 0.5f)) continue;
            return true;
        }
        return false;
    }

    private boolean checkPlayer(PlaceData data, EntityPlayer player, PositionData positionData) {
        BlockPos pos = positionData.getPos();
        if (data.getTarget() == null && player.getDistanceSq(pos) > (double)MathUtil.square(this.module.range.getValue().floatValue())) {
            return false;
        }
        boolean result = false;
        float health = EntityUtil.getHealth((EntityLivingBase)player);
        float damage = this.module.damageHelper.getDamage(pos, (EntityLivingBase)player);
        if (this.module.antiTotem.getValue().booleanValue() && !positionData.usesObby() && !positionData.isLiquid()) {
            if (this.module.antiTotemHelper.isDoublePoppable(player)) {
                if (damage > this.module.popDamage.getValue().floatValue()) {
                    data.addCorrespondingData(player, positionData);
                } else if (damage < health + this.module.maxTotemOffset.getValue().floatValue() && damage > health + this.module.minTotemOffset.getValue().floatValue()) {
                    positionData.addAntiTotem(player);
                    result = true;
                }
            } else if (this.module.forceAntiTotem.getValue().booleanValue() && Managers.COMBAT.lastPop((Entity)player) > 500L) {
                float force;
                if (damage > this.module.popDamage.getValue().floatValue()) {
                    data.confirmHighDamageForce(player);
                }
                if (damage > 0.0f && damage < this.module.totemHealth.getValue().floatValue() + this.module.maxTotemOffset.getValue().floatValue()) {
                    data.confirmPossibleAntiTotem(player);
                }
                if ((force = health - damage) > 0.0f && force < this.module.totemHealth.getValue().floatValue()) {
                    positionData.addForcePlayer(player);
                    if (force < positionData.getMinDiff()) {
                        positionData.setMinDiff(force);
                    }
                }
            }
        }
        if (damage > this.module.minFaceDmg.getValue().floatValue() && (health < this.module.facePlace.getValue().floatValue() || ((IEntityLivingBase)player).getLowestDurability() <= this.module.armorPlace.getValue().floatValue())) {
            positionData.setFacePlacer(player);
        }
        if (damage > positionData.getMaxDamage()) {
            positionData.setDamage(damage);
            positionData.setTarget(player);
        }
        return result;
    }
}

