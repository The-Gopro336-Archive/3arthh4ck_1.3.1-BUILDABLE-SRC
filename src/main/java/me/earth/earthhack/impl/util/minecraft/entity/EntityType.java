package me.earth.earthhack.impl.util.minecraft.entity;

import java.awt.Color;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityMinecart;
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
    Animal(new Color(0, 200, 0, 255)),
    Monster(new Color(200, 60, 60, 255)),
    Player(new Color(255, 255, 255, 255)),
    Boss(new Color(40, 0, 255, 255)),
    Vehicle(new Color(200, 100, 0, 255)),
    Other(new Color(200, 100, 200, 255)),
    Entity(new Color(255, 255, 0, 255));

    private final Color color;

    private EntityType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public static Supplier<EntityType> getEntityType(Entity entity) {
        if (entity instanceof EntityWolf) {
            return () -> EntityType.isAngryWolf((EntityWolf)((Object)entity)) ? Monster : Animal;
        }
        if (entity instanceof EntityEnderman) {
            return () -> EntityType.isAngryEnderMan((EntityEnderman)((Object)entity)) ? Monster : Entity;
        }
        if (entity instanceof EntityPolarBear) {
            return () -> EntityType.isAngryPolarBear((EntityPolarBear)((Object)entity)) ? Monster : Animal;
        }
        if (entity instanceof EntityPigZombie) {
            return () -> entity.rotationPitch == 0.0f && ((EntityPigZombie)((Object)entity)).func_142015_aE() <= 0 ? Monster : Entity;
        }
        if (entity instanceof EntityIronGolem) {
            return () -> EntityType.isAngryGolem((EntityIronGolem)((Object)entity)) ? Monster : Entity;
        }
        if (entity instanceof EntityVillager) {
            return () -> Entity;
        }
        if (entity instanceof EntityRabbit) {
            return () -> EntityType.isFriendlyRabbit((EntityRabbit)((Object)entity)) ? Animal : Monster;
        }
        if (EntityType.isAnimal(entity)) {
            return () -> Animal;
        }
        if (EntityType.isMonster(entity)) {
            return () -> Monster;
        }
        if (EntityType.isPlayer(entity)) {
            return () -> Player;
        }
        if (EntityType.isVehicle(entity)) {
            return () -> Vehicle;
        }
        if (EntityType.isBoss(entity)) {
            return () -> Boss;
        }
        if (EntityType.isOther(entity)) {
            return () -> Other;
        }
        return () -> Entity;
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isAnimal(Entity entity) {
        return entity instanceof EntityPig || entity instanceof EntityParrot || entity instanceof EntityCow || entity instanceof EntitySheep || entity instanceof EntityChicken || entity instanceof EntitySquid || entity instanceof EntityBat || entity instanceof EntityVillager || entity instanceof EntityOcelot || entity instanceof EntityHorse || entity instanceof EntityLlama || entity instanceof EntityMule || entity instanceof EntityDonkey || entity instanceof EntitySkeletonHorse || entity instanceof EntityZombieHorse || entity instanceof EntitySnowman || entity instanceof EntityRabbit && EntityType.isFriendlyRabbit((EntityRabbit)((Object)entity));
    }

    public static boolean isMonster(Entity entity) {
        return entity instanceof EntityCreeper || entity instanceof EntityIllusionIllager || entity instanceof EntitySkeleton || entity instanceof EntityZombie && !(entity instanceof EntityPigZombie) || entity instanceof EntityBlaze || entity instanceof EntitySpider || entity instanceof EntityWitch || entity instanceof EntitySlime || entity instanceof EntitySilverfish || entity instanceof EntityGuardian || entity instanceof EntityEndermite || entity instanceof EntityGhast || entity instanceof EntityEvoker || entity instanceof EntityShulker || entity instanceof EntityWitherSkeleton || entity instanceof EntityStray || entity instanceof EntityVex || entity instanceof EntityVindicator || entity instanceof EntityPolarBear && !EntityType.isAngryPolarBear((EntityPolarBear)((Object)entity)) || entity instanceof EntityWolf && !EntityType.isAngryWolf((EntityWolf)((Object)entity)) || entity instanceof EntityPigZombie && !EntityType.isAngryPigMan(entity) || entity instanceof EntityEnderman && !EntityType.isAngryEnderMan((EntityEnderman)((Object)entity)) || entity instanceof EntityRabbit && !EntityType.isFriendlyRabbit((EntityRabbit)((Object)entity)) || entity instanceof EntityIronGolem && !EntityType.isAngryGolem((EntityIronGolem)((Object)entity));
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

    public static boolean isAngryEnderMan(EntityEnderman enderman) {
        return enderman.isScreaming();
    }

    public static boolean isAngryPigMan(Entity entity) {
        return entity instanceof EntityPigZombie && entity.rotationPitch == 0.0f && ((EntityPigZombie)((Object)entity)).func_142015_aE() <= 0;
    }

    public static boolean isAngryGolem(EntityIronGolem ironGolem) {
        return ironGolem.field_70125_A == 0.0f;
    }

    public static boolean isAngryWolf(EntityWolf wolf) {
        return wolf.isAngry();
    }

    public static boolean isAngryPolarBear(EntityPolarBear polarBear) {
        return polarBear.field_70125_A == 0.0f && polarBear.func_142015_aE() <= 0;
    }

    public static boolean isFriendlyRabbit(EntityRabbit rabbit) {
        return rabbit.getRabbitType() != 99;
    }
}
