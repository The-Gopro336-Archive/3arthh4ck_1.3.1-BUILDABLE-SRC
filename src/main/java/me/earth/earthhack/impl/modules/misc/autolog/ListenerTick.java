/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 */
package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

final class ListenerTick
extends ModuleListener<AutoLog, TickEvent> {
    public ListenerTick(AutoLog module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.world != null && ListenerTick.mc.player != null) {
            float health;
            float f = health = ((AutoLog)this.module).absorption.getValue() != false ? EntityUtil.getHealth((EntityLivingBase)ListenerTick.mc.player) : ListenerTick.mc.player.getHealth();
            if (health <= ((AutoLog)this.module).health.getValue().floatValue()) {
                int totems;
                EntityPlayer player;
                EntityPlayer entityPlayer = player = ((AutoLog)this.module).enemy.getValue().floatValue() == 100.0f ? null : EntityUtil.getClosestEnemy();
                if ((((AutoLog)this.module).enemy.getValue().floatValue() == 100.0f || player != null && player.getDistanceSq((Entity)ListenerTick.mc.player) <= (double)MathUtil.square(((AutoLog)this.module).enemy.getValue().floatValue())) && (totems = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING)) <= ((AutoLog)this.module).totems.getValue()) {
                    ((AutoLog)this.module).disconnect(health, player, totems);
                }
            }
        }
    }
}

