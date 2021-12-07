/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

final class ListenerRightClick
extends ModuleListener<Offhand, ClickBlockEvent.Right> {
    public ListenerRightClick(Offhand module) {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event) {
        if (((Offhand)this.module).noOGC.getValue().booleanValue() && event.getHand() == EnumHand.MAIN_HAND) {
            Item mainHand = ListenerRightClick.mc.player.getHeldItemMainhand().getItem();
            Item offHand = ListenerRightClick.mc.player.getHeldItemOffhand().getItem();
            if (mainHand == Items.END_CRYSTAL && offHand == Items.GOLDEN_APPLE && event.getHand() == EnumHand.MAIN_HAND) {
                event.setCancelled(true);
                ListenerRightClick.mc.player.setActiveHand(EnumHand.OFF_HAND);
                ListenerRightClick.mc.playerController.processRightClick((EntityPlayer)ListenerRightClick.mc.player, (World)ListenerRightClick.mc.world, EnumHand.OFF_HAND);
            }
        }
    }
}

