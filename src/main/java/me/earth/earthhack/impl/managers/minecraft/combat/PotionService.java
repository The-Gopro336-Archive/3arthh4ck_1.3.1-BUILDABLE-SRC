package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.entity.living.player.IEntityPlayer;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;

public class PotionService
extends SubscriberImpl
implements Globals {
    public PotionService() {
        this.listeners.add(new ReceiveListener<SPacketEntityStatus>(SPacketEntityStatus.class, e -> {
            if (((SPacketEntityStatus)e.getPacket()).getOpCode() == 35) {
                mc.addScheduledTask(() -> this.onTotemPop((SPacketEntityStatus)e.getPacket()));
            }
        }));
        this.listeners.add(new ReceiveListener<SPacketEntityMetadata>(SPacketEntityMetadata.class, e -> mc.addScheduledTask(() -> this.onEntityMetaData((SPacketEntityMetadata)e.getPacket()))));
    }

    public void onTotemPop(SPacketEntityStatus packet) {
        if (PotionService.mc.world == null) {
            return;
        }
        Entity entity = packet.getEntity(PotionService.mc.world);
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase base = (EntityLivingBase)entity;
            base.clearActivePotions();
            base.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
            base.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
        }
    }

    public void onEntityMetaData(SPacketEntityMetadata packet) {
        if (PotionService.mc.world == null || packet.getDataManagerEntries() == null) {
            return;
        }
        Entity e = PotionService.mc.world.getEntityByID(packet.getEntityId());
        if (e instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer)e;
            for (EntityDataManager.DataEntry entry : packet.getDataManagerEntries()) {
                if (entry.getKey().getId() != IEntityPlayer.getAbsorption().getId()) continue;
                float value = ((Float)entry.getValue()).floatValue();
                float prev = p.getAbsorptionAmount();
                if (value == 4.0f && prev < value) {
                    p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
                    p.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
                    break;
                }
                if (value != 16.0f) break;
                p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
                p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
                p.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                p.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
                break;
            }
        }
    }
}
