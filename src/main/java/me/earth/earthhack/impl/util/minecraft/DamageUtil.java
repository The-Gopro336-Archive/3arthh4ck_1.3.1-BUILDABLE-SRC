/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockAnvil
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.CombatRules
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.core.mixins.item.IItemTool;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockWeb;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DamageUtil
implements Globals {
    public static boolean isSharper(ItemStack stack, int level) {
        return EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.SHARPNESS, (ItemStack)stack) > level;
    }

    public static boolean canBreakWeakness(boolean checkStack) {
        if (!DamageUtil.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            return true;
        }
        int strengthAmp = 0;
        PotionEffect effect = DamageUtil.mc.player.getActivePotionEffect(MobEffects.STRENGTH);
        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }
        if (strengthAmp >= 1) {
            return true;
        }
        return checkStack && DamageUtil.canBreakWeakness(DamageUtil.mc.player.getHeldItemMainhand());
    }

    public static boolean isWeaknessed() {
        try {
            return !DamageUtil.canBreakWeakness(true);
        }
        catch (Throwable t) {
            return true;
        }
    }

    public static boolean cacheLowestDura(EntityLivingBase base) {
        IEntityLivingBase access = (IEntityLivingBase)base;
        float before = access.getLowestDurability();
        access.setLowestDura(Float.MAX_VALUE);
        try {
            boolean isNaked = true;
            for (ItemStack stack : base.getArmorInventoryList()) {
                if (stack.isEmpty()) continue;
                isNaked = false;
                float damage = DamageUtil.getPercent(stack);
                if (!(damage < access.getLowestDurability())) continue;
                access.setLowestDura(damage);
            }
            return isNaked;
        }
        catch (Throwable t) {
            t.printStackTrace();
            access.setLowestDura(before);
            return false;
        }
    }

    public static boolean canBreakWeakness(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            return true;
        }
        if (stack.getItem() instanceof ItemTool) {
            IItemTool tool = (IItemTool)stack.getItem();
            return tool.getAttackDamage() > 4.0f;
        }
        return false;
    }

    public static int findAntiWeakness() {
        int slot = -1;
        for (int i = 8; i > -1; --i) {
            if (!DamageUtil.canBreakWeakness(DamageUtil.mc.player.inventory.getStackInSlot(i))) continue;
            slot = i;
            if (DamageUtil.mc.player.inventory.currentItem == i) break;
        }
        return slot;
    }

    public static int getDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getPercent(ItemStack stack) {
        return (float)DamageUtil.getDamage(stack) / (float)stack.getMaxDamage() * 100.0f;
    }

    public static float calculate(Entity crystal) {
        return DamageUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)RotationUtil.getRotationPlayer());
    }

    public static float calculate(BlockPos pos) {
        return DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, (EntityLivingBase)RotationUtil.getRotationPlayer());
    }

    public static float calculate(BlockPos p, EntityLivingBase base) {
        return DamageUtil.calculate((float)p.getX() + 0.5f, p.getY() + 1, (float)p.getZ() + 0.5f, base);
    }

    public static float calculate(BlockPos p, EntityLivingBase base, IBlockAccess world) {
        return DamageUtil.calculate((float)p.getX() + 0.5f, p.getY() + 1, (float)p.getZ() + 0.5f, base.getEntityBoundingBox(), base, world, false);
    }

    public static float calculate(Entity crystal, EntityLivingBase base) {
        return DamageUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, base);
    }

    public static float calculate(double x, double y, double z, EntityLivingBase base) {
        return DamageUtil.calculate(x, y, z, base.getEntityBoundingBox(), base);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base) {
        return DamageUtil.calculate(x, y, z, bb, base, false);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, boolean terrainCalc) {
        return DamageUtil.calculate(x, y, z, bb, base, (IBlockAccess)DamageUtil.mc.world, terrainCalc);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc) {
        return DamageUtil.calculate(x, y, z, bb, base, world, terrainCalc, false);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc, boolean anvils) {
        return DamageUtil.calculate(x, y, z, bb, base, world, terrainCalc, anvils, 6.0f);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc, boolean anvils, float power) {
        double distance = base.getDistance(x, y, z) / 12.0;
        if (distance > 1.0) {
            return 0.0f;
        }
        double density = DamageUtil.getBlockDensity(new Vec3d(x, y, z), bb, world, true, true, terrainCalc, anvils);
        double densityDistance = distance = (1.0 - distance) * density;
        float damage = DamageUtil.getDifficultyMultiplier((float)((densityDistance * densityDistance + distance) / 2.0 * 7.0 * 12.0 + 1.0));
        DamageSource damageSource = DamageSource.causeExplosionDamage((Explosion)new Explosion((World)DamageUtil.mc.world, (Entity)DamageUtil.mc.player, x, y, z, power, false, true));
        ICachedDamage cache = (ICachedDamage)base;
        int armorValue = cache.getArmorValue();
        float toughness = cache.getArmorToughness();
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)armorValue, (float)toughness);
        PotionEffect resistance = base.getActivePotionEffect(MobEffects.RESISTANCE);
        if (resistance != null) {
            damage = damage * (float)(25 - (resistance.getAmplifier() + 1) * 5) / 25.0f;
        }
        if (damage <= 0.0f) {
            return 0.0f;
        }
        int modifierDamage = cache.getExplosionModifier(damageSource);
        if (modifierDamage > 0) {
            damage = CombatRules.getDamageAfterMagicAbsorb((float)damage, (float)modifierDamage);
        }
        return Math.max(damage, 0.0f);
    }

    public static float getDifficultyMultiplier(float distance) {
        switch (DamageUtil.mc.world.getDifficulty()) {
            case PEACEFUL: {
                return 0.0f;
            }
            case EASY: {
                return Math.min(distance / 2.0f + 1.0f, distance);
            }
            case HARD: {
                return distance * 3.0f / 2.0f;
            }
        }
        return distance;
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb, IBlockAccess world, boolean ignoreWebs, boolean ignoreBeds, boolean terrainCalc, boolean anvils) {
        double x = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double y = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double z = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double xFloor = (1.0 - Math.floor(1.0 / x) * x) / 2.0;
        double zFloor = (1.0 - Math.floor(1.0 / z) * z) / 2.0;
        if (x >= 0.0 && y >= 0.0 && z >= 0.0) {
            int air = 0;
            int traced = 0;
            float a = 0.0f;
            while (a <= 1.0f) {
                float b = 0.0f;
                while (b <= 1.0f) {
                    float c = 0.0f;
                    while (c <= 1.0f) {
                        double xOff = bb.minX + (bb.maxX - bb.minX) * (double)a;
                        double yOff = bb.minY + (bb.maxY - bb.minY) * (double)b;
                        double zOff = bb.minZ + (bb.maxZ - bb.minZ) * (double)c;
                        RayTraceResult result = DamageUtil.rayTraceBlocks(new Vec3d(xOff + xFloor, yOff, zOff + zFloor), vec, world, false, false, false, ignoreWebs, ignoreBeds, terrainCalc, anvils);
                        if (result == null) {
                            ++air;
                        }
                        ++traced;
                        c = (float)((double)c + z);
                    }
                    b = (float)((double)b + y);
                }
                a = (float)((double)a + x);
            }
            return (float)air / (float)traced;
        }
        return 0.0f;
    }

    public static RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, IBlockAccess world, boolean stopOnLiquid, boolean ignoreNoBox, boolean lastUncollidableBlock, boolean ignoreWebs, boolean ignoreBeds, boolean terrainCalc, boolean anvils) {
        return RayTracer.trace((World)DamageUtil.mc.world, world, start, end, stopOnLiquid, ignoreNoBox, lastUncollidableBlock, (b, p) -> !((terrainCalc && b.getExplosionResistance((Entity)DamageUtil.mc.player) < 100.0f && p.distanceSq(end.x, end.y, end.z) <= 36.0 || ignoreBeds && b instanceof BlockBed || ignoreWebs && b instanceof BlockWeb) && (!anvils || !(b instanceof BlockAnvil))));
    }
}

