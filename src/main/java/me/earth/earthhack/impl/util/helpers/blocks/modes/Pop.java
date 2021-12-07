/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 */
package me.earth.earthhack.impl.util.helpers.blocks.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;

public enum Pop implements Globals
{
    None{

        @Override
        public boolean shouldPop(float damage, int popTime) {
            return (double)damage < (double)EntityUtil.getHealth((EntityLivingBase)1.mc.player) + 1.0;
        }
    }
    ,
    Time{

        @Override
        public boolean shouldPop(float damage, int popTime) {
            return None.shouldPop(damage, popTime) || 2.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING && Managers.COMBAT.lastPop((Entity)2.mc.player) < (long)popTime;
        }
    }
    ,
    Always{

        @Override
        public boolean shouldPop(float damage, int popTime) {
            return None.shouldPop(damage, popTime) || 3.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
        }
    };


    public abstract boolean shouldPop(float var1, int var2);
}

