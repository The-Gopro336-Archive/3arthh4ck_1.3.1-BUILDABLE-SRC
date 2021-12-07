package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHelper;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DamageHelper
implements Globals {
    private final Setting<Boolean> terrainCalc;
    private final Setting<Integer> bExtrapolation;
    private final Setting<Integer> pExtrapolation;
    private final Setting<Boolean> selfExtrapolation;
    private final Setting<Boolean> obbyTerrain;
    private final PositionHelper positionHelper;

    public DamageHelper(PositionHelper positionHelper, Setting<Boolean> terrainCalc, Setting<Integer> extrapolation, Setting<Integer> bExtrapolation, Setting<Boolean> selfExtrapolation, Setting<Boolean> obbyTerrain) {
        this.positionHelper = positionHelper;
        this.terrainCalc = terrainCalc;
        this.pExtrapolation = extrapolation;
        this.bExtrapolation = bExtrapolation;
        this.selfExtrapolation = selfExtrapolation;
        this.obbyTerrain = obbyTerrain;
    }

    public float getDamage(Entity crystal) {
        return this.getDamage(crystal.posX, crystal.posY, crystal.posZ, Managers.POSITION.getBB(), DamageHelper.mc.player);
    }

    public float getDamage(Entity crystal, AxisAlignedBB bb) {
        return DamageUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, bb, DamageHelper.mc.player);
    }

    public float getDamage(Entity crystal, EntityLivingBase base) {
        if (this.bExtrapolation.getValue() != 0) {
            return this.getDamage(crystal.posX, crystal.posY, crystal.posZ, this.extrapolateEntity(base, this.bExtrapolation.getValue()), base);
        }
        return this.getDamage(crystal.posX, crystal.posY, crystal.posZ, base);
    }

    public float getDamage(BlockPos pos) {
        return this.getDamage(pos, (EntityLivingBase)RotationUtil.getRotationPlayer());
    }

    public float getDamage(BlockPos pos, EntityLivingBase base) {
        if (this.pExtrapolation.getValue() != 0 && (this.selfExtrapolation.getValue().booleanValue() || !base.equals(RotationUtil.getRotationPlayer()))) {
            return this.getDamage((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, this.extrapolateEntity(base, this.pExtrapolation.getValue()), base);
        }
        return this.getDamage((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, base);
    }

    public float getDamage(double x, double y, double z, EntityLivingBase base) {
        return this.getDamage(x, y, z, base.getEntityBoundingBox(), base);
    }

    public float getDamage(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base) {
        return DamageUtil.calculate(x, y, z, bb, base, this.terrainCalc.getValue());
    }

    public float getObbyDamage(BlockPos pos, IBlockStateHelper world) {
        AxisAlignedBB bb = this.selfExtrapolation.getValue() != false ? this.extrapolateEntity(RotationUtil.getRotationPlayer(), this.pExtrapolation.getValue()) : RotationUtil.getRotationPlayer().getEntityBoundingBox();
        return this.getObbyDamage(pos, DamageHelper.mc.player, bb, world);
    }

    public float getObbyDamage(BlockPos pos, EntityLivingBase base, IBlockStateHelper world) {
        return this.getObbyDamage(pos, base, this.extrapolateEntity(base, this.pExtrapolation.getValue()), world);
    }

    public float getObbyDamage(BlockPos pos, EntityLivingBase base, AxisAlignedBB bb, IBlockStateHelper world) {
        return DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, bb, base, world, this.obbyTerrain.getValue());
    }

    public AxisAlignedBB extrapolateEntity(Entity entity, int ticks) {
        if (ticks == 0) {
            return entity.getEntityBoundingBox();
        }
        MotionTracker tracker = this.positionHelper.getTrackerFromEntity(entity);
        if (tracker == null) {
            return entity.getEntityBoundingBox();
        }
        MotionTracker copy = new MotionTracker((World)DamageHelper.mc.world, tracker);
        for (int i = 0; i < ticks; ++i) {
            copy.updateSilent();
        }
        return copy.getEntityBoundingBox();
    }
}
