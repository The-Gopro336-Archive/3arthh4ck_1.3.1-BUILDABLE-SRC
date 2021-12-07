/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.core.mixins.gui.IGuiContainer;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

final class ListenerTick
extends ModuleListener<ToolTips, TickEvent> {
    public ListenerTick(ToolTips module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            ItemStack stack;
            Slot slot;
            if (ListenerTick.mc.currentScreen instanceof IGuiContainer && KeyBoardUtil.isKeyDown(((ToolTips)this.module).peekBind) && (slot = ((IGuiContainer)ListenerTick.mc.currentScreen).getHoveredSlot()) != null && (stack = slot.getStack()).getItem() instanceof ItemShulkerBox) {
                ((ToolTips)this.module).displayInventory(stack, null);
            }
            if (((ToolTips)this.module).shulkerSpy.getValue().booleanValue()) {
                for (EntityPlayer player : ListenerTick.mc.world.playerEntities) {
                    if (player == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) || PlayerUtil.isFakePlayer((Entity)player) || !((ToolTips)this.module).own.getValue().booleanValue() && ListenerTick.mc.player.equals((Object)player)) continue;
                    ItemStack stack2 = player.getHeldItemMainhand();
                    ((ToolTips)this.module).spiedPlayers.put(player.getName().toLowerCase(), new TimeStack(stack2, System.nanoTime()));
                }
            }
        }
    }
}

