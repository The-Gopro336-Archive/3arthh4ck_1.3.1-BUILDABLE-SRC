/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.world.World
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

final class ListenerMotion
extends ModuleListener<ExpTweaks, MotionUpdateEvent> {
    public ListenerMotion(ExpTweaks module) {
        super(module, MotionUpdateEvent.class, 1000);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            if (((ExpTweaks)this.module).feetExp.getValue().booleanValue() && (InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && Mouse.isButtonDown((int)1) || ((ExpTweaks)this.module).isMiddleClick())) {
                event.setPitch(90.0f);
            }
        } else if (!(!((ExpTweaks)this.module).isMiddleClick() || ((ExpTweaks)this.module).wasteStop.getValue().booleanValue() && ((ExpTweaks)this.module).isWasting() || !((ExpTweaks)this.module).whileEating.getValue().booleanValue() && ListenerMotion.mc.player.getActiveItemStack().getItem() instanceof ItemFood)) {
            int slot = InventoryUtil.findHotbarItem(Items.EXPERIENCE_BOTTLE, new Item[0]);
            if (slot != -1) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    int lastSlot = ListenerMotion.mc.player.inventory.currentItem;
                    boolean silent = ((ExpTweaks)this.module).silent.getValue();
                    if (silent) {
                        ((ExpTweaks)this.module).isMiddleClick = true;
                    }
                    InventoryUtil.switchTo(slot);
                    ListenerMotion.mc.playerController.processRightClick((EntityPlayer)ListenerMotion.mc.player, (World)ListenerMotion.mc.world, InventoryUtil.getHand(slot));
                    if (silent) {
                        InventoryUtil.switchTo(lastSlot);
                        ((ExpTweaks)this.module).isMiddleClick = false;
                        ((ExpTweaks)this.module).lastSlot = -1;
                    } else if (lastSlot != slot) {
                        ((ExpTweaks)this.module).lastSlot = lastSlot;
                    }
                });
            } else if (((ExpTweaks)this.module).lastSlot != -1) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    InventoryUtil.switchTo(((ExpTweaks)this.module).lastSlot);
                    ((ExpTweaks)this.module).lastSlot = -1;
                });
            }
        } else if (((ExpTweaks)this.module).lastSlot != -1) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                InventoryUtil.switchTo(((ExpTweaks)this.module).lastSlot);
                ((ExpTweaks)this.module).lastSlot = -1;
            });
        }
    }
}

