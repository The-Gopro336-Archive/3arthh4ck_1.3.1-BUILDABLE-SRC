/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.boss.EntityWither
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityEnderEye
 *  net.minecraft.entity.item.EntityEnderPearl
 *  net.minecraft.entity.item.EntityFallingBlock
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.item.EntityMinecartEmpty
 *  net.minecraft.entity.item.EntityMinecartFurnace
 *  net.minecraft.entity.item.EntityMinecartTNT
 *  net.minecraft.entity.monster.EntityBlaze
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.monster.EntityEndermite
 *  net.minecraft.entity.monster.EntityEvoker
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.entity.monster.EntityGiantZombie
 *  net.minecraft.entity.monster.EntityGuardian
 *  net.minecraft.entity.monster.EntityIllusionIllager
 *  net.minecraft.entity.monster.EntityIronGolem
 *  net.minecraft.entity.monster.EntityPigZombie
 *  net.minecraft.entity.monster.EntityPolarBear
 *  net.minecraft.entity.monster.EntityShulker
 *  net.minecraft.entity.monster.EntitySilverfish
 *  net.minecraft.entity.monster.EntitySkeleton
 *  net.minecraft.entity.monster.EntitySlime
 *  net.minecraft.entity.monster.EntitySnowman
 *  net.minecraft.entity.monster.EntitySpider
 *  net.minecraft.entity.monster.EntityStray
 *  net.minecraft.entity.monster.EntityVex
 *  net.minecraft.entity.monster.EntityVindicator
 *  net.minecraft.entity.monster.EntityWitch
 *  net.minecraft.entity.monster.EntityWitherSkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.entity.passive.EntityBat
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityDonkey
 *  net.minecraft.entity.passive.EntityHorse
 *  net.minecraft.entity.passive.EntityLlama
 *  net.minecraft.entity.passive.EntityMule
 *  net.minecraft.entity.passive.EntityOcelot
 *  net.minecraft.entity.passive.EntityParrot
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntityRabbit
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.passive.EntitySkeletonHorse
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWolf
 *  net.minecraft.entity.passive.EntityZombieHorse
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityEvokerFangs
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.entity.projectile.EntityShulkerBullet
 */
package me.earth.earthhack.impl.util.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;

public enum EntityType {
    Animal(EntityType::isAnimal),
    Player(EntityType::isPlayer),
    Boss(EntityType::isBoss),
    Monster(EntityType::isMonster),
    Vehicle(EntityType::isVehicle),
    Other(EntityType::isOther);

    private static final Map<Class<? extends Entity>, String> entityNames;
    final Predicate<Entity> predicate;

    private EntityType(Predicate<Entity> predicate) {
        this.predicate = predicate;
    }

    public boolean is(Entity entity) {
        return this.predicate.test(entity);
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isAnimal(Entity entity) {
        return entity instanceof EntityPig || entity instanceof EntityParrot || entity instanceof EntityCow || entity instanceof EntitySheep || entity instanceof EntityChicken || entity instanceof EntitySquid || entity instanceof EntityBat || entity instanceof EntityVillager || entity instanceof EntityOcelot || entity instanceof EntityHorse || entity instanceof EntityLlama || entity instanceof EntityMule || entity instanceof EntityDonkey || entity instanceof EntitySkeletonHorse || entity instanceof EntityZombieHorse || entity instanceof EntitySnowman || entity instanceof EntityWolf || entity instanceof EntityRabbit && EntityType.isFriendlyRabbit(entity);
    }

    public static boolean isMonster(Entity entity) {
        return entity instanceof EntityCreeper || entity instanceof EntityIllusionIllager || entity instanceof EntitySkeleton || entity instanceof EntityZombie || entity instanceof EntityBlaze || entity instanceof EntitySpider || entity instanceof EntityWitch || entity instanceof EntitySlime || entity instanceof EntitySilverfish || entity instanceof EntityGuardian || entity instanceof EntityEndermite || entity instanceof EntityGhast || entity instanceof EntityEvoker || entity instanceof EntityShulker || entity instanceof EntityWitherSkeleton || entity instanceof EntityStray || entity instanceof EntityVex || entity instanceof EntityVindicator || entity instanceof EntityPolarBear || entity instanceof EntityWolf || entity instanceof EntityEnderman || entity instanceof EntityRabbit || entity instanceof EntityIronGolem;
    }

    public static boolean isBoss(Entity entity) {
        return entity instanceof EntityDragon || entity instanceof EntityWither || entity instanceof EntityGiantZombie;
    }

    public static boolean isOther(Entity entity) {
        return entity instanceof EntityEnderCrystal || entity instanceof EntityEvokerFangs || entity instanceof EntityShulkerBullet || entity instanceof EntityFallingBlock || entity instanceof EntityFireball || entity instanceof EntityEnderEye || entity instanceof EntityEnderPearl;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isAngry(Entity entity) {
        return entity instanceof EntityWolf && EntityType.isAngryWolf(entity) || entity instanceof EntityPolarBear && EntityType.isAngryPolarBear(entity) || entity instanceof EntityIronGolem && EntityType.isAngryGolem(entity) || entity instanceof EntityEnderman && EntityType.isAngryEnderMan(entity) || entity instanceof EntityPigZombie && EntityType.isAngryPigMan(entity);
    }

    public static boolean isAngryEnderMan(Entity entity) {
        return entity instanceof EntityEnderman && !((EntityEnderman)entity).isScreaming();
    }

    public static boolean isAngryPigMan(Entity entity) {
        return entity instanceof EntityPigZombie && entity.rotationPitch == 0.0f && ((EntityPigZombie)entity).getRevengeTimer() <= 0;
    }

    public static boolean isAngryGolem(Entity entity) {
        return entity instanceof EntityIronGolem && entity.rotationPitch == 0.0f;
    }

    public static boolean isAngryWolf(Entity entity) {
        return entity instanceof EntityWolf && !((EntityWolf)entity).isAngry();
    }

    public static boolean isAngryPolarBear(Entity entity) {
        return entity instanceof EntityPolarBear && entity.rotationPitch == 0.0f && ((EntityPolarBear)entity).getRevengeTimer() <= 0;
    }

    public static boolean isFriendlyRabbit(Entity entity) {
        return entity instanceof EntityRabbit && ((EntityRabbit)entity).getRabbitType() != 99;
    }

    public static String getName(Entity entity) {
        String name = entityNames.get(entity.getClass());
        if (name != null) {
            return name;
        }
        return entity.getName();
    }

    static {
        entityNames = new HashMap<Class<? extends Entity>, String>();
        entityNames.put(EntityItemFrame.class, "Item Frame");
        entityNames.put(EntityEnderCrystal.class, "End Crystal");
        entityNames.put(EntityMinecartEmpty.class, "Minecart");
        entityNames.put(EntityMinecart.class, "Minecart");
        entityNames.put(EntityMinecartFurnace.class, "Minecart with Furnace");
        entityNames.put(EntityMinecartTNT.class, "Minecart with TNT");
    }
}

