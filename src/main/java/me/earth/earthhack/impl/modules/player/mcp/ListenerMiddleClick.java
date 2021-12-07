/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.mcp.MiddleClickPearl;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

final class ListenerMiddleClick
extends ModuleListener<MiddleClickPearl, ClickMiddleEvent> {
    protected ListenerMiddleClick(MiddleClickPearl module) {
        super(module, ClickMiddleEvent.class, 11);
    }

    @Override
    public void invoke(ClickMiddleEvent event) {
        if (!event.isModuleCancelled() && !event.isCancelled()) {
            if (InventoryUtil.findHotbarItem(Items.ENDER_PEARL, new Item[0]) == -1) {
                return;
            }
            if (!((MiddleClickPearl)this.module).prioritizeMCF()) {
                if (((MiddleClickPearl)this.module).cancelBlock.getValue().booleanValue()) {
                    event.setCancelled(true);
                }
            } else if (((MiddleClickPearl)this.module).cancelMCF.getValue().booleanValue()) {
                event.setModuleCancelled(true);
            } else {
                return;
            }
            ((MiddleClickPearl)this.module).runnable = () -> {
                int slot = InventoryUtil.findHotbarItem(Items.ENDER_PEARL, new Item[0]);
                if (slot == -1) {
                    return;
                }
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    int lastSlot = ListenerMiddleClick.mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);
                    ListenerMiddleClick.mc.playerController.processRightClick((EntityPlayer)ListenerMiddleClick.mc.player, (World)ListenerMiddleClick.mc.world, InventoryUtil.getHand(slot));
                    InventoryUtil.switchTo(lastSlot);
                });
            };
            if (Managers.ROTATION.getServerPitch() == ListenerMiddleClick.mc.player.rotationPitch && Managers.ROTATION.getServerYaw() == ListenerMiddleClick.mc.player.rotationYaw) {
                ((MiddleClickPearl)this.module).runnable.run();
                ((MiddleClickPearl)this.module).runnable = null;
            }
        }
    }
}

