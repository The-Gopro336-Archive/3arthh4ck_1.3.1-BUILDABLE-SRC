/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.INetHandlerPlayClient
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.EntityPlayerAttack;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityPlayerPop
extends EntityPlayerAttack {
    public EntityPlayerPop(World worldIn) {
        super(worldIn);
    }

    public EntityPlayerPop(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        if (slotIn == EntityEquipmentSlot.OFFHAND) {
            ItemStack stack = new ItemStack(Items.TOTEM_OF_UNDYING);
            stack.setCount(1);
            return stack;
        }
        return super.getItemStackFromSlot(slotIn);
    }

    public void setHealth(float health) {
        if (health <= 0.0f) {
            this.pop();
            return;
        }
        super.setHealth(health);
    }

    public void setDead() {
    }

    public void pop() {
        NetworkUtil.receive((Packet<INetHandlerPlayClient>)new SPacketEntityStatus((Entity)this, 35));
        super.setHealth(1.0f);
        this.setAbsorptionAmount(8.0f);
        this.clearActivePotions();
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
        this.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
    }
}

