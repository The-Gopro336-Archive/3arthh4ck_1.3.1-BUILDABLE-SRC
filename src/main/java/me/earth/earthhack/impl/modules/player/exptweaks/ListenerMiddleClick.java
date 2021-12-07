/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 */
package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

final class ListenerMiddleClick
extends ModuleListener<ExpTweaks, ClickMiddleEvent> {
    public ListenerMiddleClick(ExpTweaks module) {
        super(module, ClickMiddleEvent.class);
    }

    @Override
    public void invoke(ClickMiddleEvent event) {
        int slot;
        if (((ExpTweaks)this.module).middleClickExp.getValue().booleanValue() && ((ExpTweaks)this.module).mceBind.getValue().getKey() == -1 && !event.isModuleCancelled() && !event.isCancelled() && !((ExpTweaks)this.module).isWasting() && (slot = InventoryUtil.findHotbarItem(Items.EXPERIENCE_BOTTLE, new Item[0])) != -1 && slot != -2 && slot != ListenerMiddleClick.mc.player.inventory.currentItem) {
            event.setCancelled(true);
        }
    }
}

