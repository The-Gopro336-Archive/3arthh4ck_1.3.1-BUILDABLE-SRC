/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class HelperObby
implements Globals {
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD = Caches.getSetting(Safety.class, NumberSetting.class, "MaxDamage", Float.valueOf(4.0f));
    private final AutoCrystal module;

    public HelperObby(AutoCrystal module) {
        this.module = module;
    }

    public PositionData findBestObbyData(Map<BlockPos, PositionData> obbyData, List<EntityPlayer> players, List<EntityPlayer> friends, List<Entity> entities, EntityPlayer target, boolean newVer) {
        int maxPath;
        double maxY = 0.0;
        LinkedList<EntityPlayer> filteredPlayers = new LinkedList<EntityPlayer>();
        for (EntityPlayer player : players) {
            if (player == null || EntityUtil.isDead((Entity)player) || player.posY > HelperObby.mc.player.posY + 18.0 || player.getDistanceSq((Entity)HelperObby.mc.player) > (double)MathUtil.square(this.module.targetRange.getValue().floatValue())) continue;
            filteredPlayers.add(player);
            if (!(player.posY > maxY)) continue;
            maxY = player.posY;
        }
        int fastObby = this.module.fastObby.getValue();
        if (fastObby != 0) {
            HashSet<BlockPos> positions;
            if (target != null) {
                positions = new HashSet<BlockPos>((int)((double)(4 * fastObby) / 0.75) + 1);
                this.addPositions(positions, target, fastObby);
            } else {
                positions = new HashSet((int)((double)(filteredPlayers.size() * 4 * fastObby) / 0.75 + 1.0));
                for (EntityPlayer player : filteredPlayers) {
                    this.addPositions(positions, player, fastObby);
                }
            }
            obbyData.keySet().retainAll(positions);
        }
        int shortest = maxPath = this.module.helpingBlocks.getValue().intValue();
        float maxDamage = 0.0f;
        float maxSelfDamage = 0.0f;
        PositionData bestData = null;
        for (PositionData positionData : obbyData.values()) {
            boolean betterDmg;
            BlockPos pos;
            if (positionData.isBlocked() || (double)(pos = positionData.getPos()).getY() >= maxY) continue;
            float self = Float.MAX_VALUE;
            boolean preSelf = this.module.obbyPreSelf.getValue();
            BlockStateHelper helper = new BlockStateHelper(new HashMap<BlockPos, IBlockState>());
            if (preSelf) {
                helper.addBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                self = this.module.damageHelper.getObbyDamage(pos, helper);
                if (this.checkSelfDamage(self)) continue;
                positionData.setSelfDamage(self);
            }
            BlockPos[] ignore = new BlockPos[newVer ? 1 : 2];
            ignore[0] = pos.up();
            if (!newVer) {
                ignore[1] = pos.up(2);
            }
            if (this.module.interact.getValue().booleanValue()) {
                Ray[] mode = this.module.obbyTrace.getValue();
                EnumFacing[] arrenumFacing = EnumFacing.values();
                int n = arrenumFacing.length;
                for (int i = 0; i < n; ++i) {
                    IBlockState state;
                    EnumFacing facing = arrenumFacing[i];
                    BlockPos offset = pos.offset(facing);
                    if (BlockUtil.getDistanceSq(offset) >= (double)MathUtil.square(this.module.placeRange.getValue().floatValue()) || (state = HelperObby.mc.world.getBlockState(offset)).getMaterial().isReplaceable() && !state.getMaterial().isLiquid()) continue;
                    Ray ray = RayTraceFactory.rayTrace(positionData.getFrom(), offset, facing.getOpposite(), (IBlockAccess)HelperObby.mc.world, Blocks.OBSIDIAN.getDefaultState(), mode == RayTraceMode.Smart ? -1.0 : 2.0);
                    if (!ray.isLegit() && mode == RayTraceMode.Smart) continue;
                    if (this.module.inside.getValue().booleanValue() && state.getMaterial().isLiquid()) {
                        ray.getResult().sideHit = ray.getResult().sideHit.getOpposite();
                        ray = new Ray(ray.getResult(), ray.getRotations(), ray.getPos().offset(ray.getFacing()), ray.getFacing().getOpposite(), ray.getVector());
                    }
                    positionData.setValid(true);
                    positionData.setPath(ray);
                    break;
                }
            }
            if (!positionData.isValid()) {
                PathFinder.findPath(positionData, this.module.placeRange.getValue().floatValue(), entities, this.module.obbyTrace.getValue(), helper, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, ignore);
            }
            if (!positionData.isValid() || positionData.getPath() == null || positionData.getPath().length > maxPath) continue;
            for (Ray ray : positionData.getPath()) {
                helper.addBlockState(ray.getPos().offset(ray.getFacing()), Blocks.OBSIDIAN.getDefaultState());
            }
            if (!preSelf) {
                self = this.module.damageHelper.getObbyDamage(pos, helper);
                if (this.checkSelfDamage(self)) continue;
                positionData.setSelfDamage(self);
            }
            if (this.module.antiFriendPop.getValue().shouldCalc(AntiFriendPop.Place)) {
                boolean poppingFriend = false;
                for (EntityPlayer friend : friends) {
                    float damage = this.module.damageHelper.getObbyDamage(pos, (EntityLivingBase)friend, helper);
                    if (!(damage > EntityUtil.getHealth((EntityLivingBase)friend))) continue;
                    poppingFriend = true;
                    break;
                }
                if (poppingFriend) continue;
            }
            float damage = 0.0f;
            if (target != null) {
                positionData.setTarget(target);
                damage = this.module.damageHelper.getObbyDamage(pos, (EntityLivingBase)target, helper);
                if (damage < this.module.minDamage.getValue().floatValue()) {
                    continue;
                }
            } else {
                for (EntityPlayer p : filteredPlayers) {
                    float d = this.module.damageHelper.getObbyDamage(pos, (EntityLivingBase)p, helper);
                    if (d < this.module.minDamage.getValue().floatValue() || d < damage) continue;
                    damage = d;
                    positionData.setTarget(p);
                }
            }
            if (damage < this.module.minDamage.getValue().floatValue()) continue;
            positionData.setDamage(damage);
            int length = positionData.getPath().length;
            if (bestData == null) {
                bestData = positionData;
                maxDamage = damage;
                maxSelfDamage = self;
                shortest = length;
                continue;
            }
            boolean betterLen = length - this.module.maxDiff.getValue() < shortest;
            boolean bl = betterDmg = (double)damage + this.module.maxDmgDiff.getValue() > (double)maxDamage && (double)damage - this.module.maxDmgDiff.getValue() >= (double)this.module.minDamage.getValue().floatValue();
            if (!(betterLen && damage > maxDamage || betterDmg && length < shortest) && (!betterDmg || length != shortest || !(self < maxSelfDamage))) continue;
            bestData = positionData;
            if (length < shortest) {
                shortest = length;
            }
            if (damage > maxDamage) {
                maxDamage = damage;
            }
            if (!(self < maxSelfDamage)) continue;
            maxSelfDamage = self;
        }
        return bestData;
    }

    private void addPositions(Set<BlockPos> positions, EntityPlayer player, int fastObby) {
        BlockPos down = PositionUtil.getPosition((Entity)player).down();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset = down;
            for (int i = 0; i < fastObby; ++i) {
                offset = offset.offset(facing);
                positions.add(offset);
            }
        }
    }

    private boolean checkSelfDamage(float self) {
        if (self > MD.getValue().floatValue() && this.module.obbySafety.getValue().booleanValue()) {
            Managers.SAFETY.setSafe(false);
        }
        if ((double)self > (double)EntityUtil.getHealth((EntityLivingBase)HelperObby.mc.player) - 1.0) {
            if (this.module.obbySafety.getValue().booleanValue()) {
                Managers.SAFETY.setSafe(false);
            }
            return true;
        }
        return self > this.module.maxSelfPlace.getValue().floatValue();
    }
}

