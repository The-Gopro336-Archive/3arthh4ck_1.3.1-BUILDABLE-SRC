/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.replenish.Replenish;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

final class ListenerTick
extends ModuleListener<Replenish, TickEvent> {
    private static final ModuleCache<XCarry> XCARRY = Caches.getModule(XCarry.class);
    private boolean reset;

    public ListenerTick(Replenish module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe() && !ListenerTick.mc.player.isCreative() && !(ListenerTick.mc.currentScreen instanceof GuiContainer)) {
            this.reset = false;
            if (!((Replenish)this.module).timer.passed(((Replenish)this.module).delay.getValue().intValue()) || !((Replenish)this.module).replenishInLoot.getValue().booleanValue() && !ListenerTick.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox()).isEmpty()) {
                return;
            }
            for (int i = 0; i < 9; ++i) {
                int slot;
                ItemStack before;
                ItemStack stack = ListenerTick.mc.player.inventory.getStackInSlot(i);
                Item iItem = stack.getItem();
                if (((Replenish)this.module).isStackValid(stack) && stack.getCount() <= ((Replenish)this.module).threshold.getValue() && (stack.getCount() < stack.getMaxStackSize() || stack.isEmpty()) && (before = ((Replenish)this.module).hotbar.get(i)) != null && ((Replenish)this.module).isStackValid(stack) && !before.isEmpty() && (before.getItem() == stack.getItem() || stack.isEmpty()) && (slot = this.findSlot(stack.isEmpty() ? before : stack, stack.getCount())) != -1) {
                    boolean drag = slot == -2;
                    boolean diff = false;
                    if (slot > 46) {
                        if ((slot -= 100) < 1) {
                            ((Replenish)this.module).hotbar.set(i, stack.copy());
                            continue;
                        }
                        diff = true;
                    }
                    int finalI = i + 36;
                    int finalSlot = slot;
                    boolean finalDiff = diff;
                    Item sItem = InventoryUtil.get(slot).getItem();
                    Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                        if (InventoryUtil.get(finalI).getItem() != iItem || InventoryUtil.get(finalSlot).getItem() != sItem) {
                            return;
                        }
                        Managers.NCP.startMultiClick();
                        if (!drag) {
                            InventoryUtil.click(finalSlot);
                        }
                        InventoryUtil.click(finalI);
                        if (finalDiff && ((Replenish)this.module).putBack.getValue().booleanValue()) {
                            InventoryUtil.click(finalSlot);
                        }
                        Managers.NCP.releaseMultiClick();
                    });
                    ((Replenish)this.module).timer.reset();
                    if (((Replenish)this.module).delay.getValue() == 0) continue;
                    break;
                }
                ((Replenish)this.module).hotbar.set(i, stack.copy());
            }
        } else if (!this.reset) {
            ((Replenish)this.module).clear();
            this.reset = true;
        }
    }

    private int findSlot(ItemStack current, int count) {
        int result = -1;
        int maxDiff = current.getMaxStackSize() - count;
        int minSize = current.getMaxStackSize() > ((Replenish)this.module).minSize.getValue() ? ((Replenish)this.module).minSize.getValue().intValue() : current.getMaxStackSize();
        int maxSize = 0;
        int maxIndex = -1;
        int limitSize = 0;
        if (InventoryUtil.canStack(current, ListenerTick.mc.player.inventory.getItemStack())) {
            return -2;
        }
        boolean xCarry = XCARRY.isEnabled();
        for (int i = 9; i <= 36 && i != 5; ++i) {
            ItemStack stack;
            if (i == 36) {
                if (!xCarry) break;
                i = 1;
            }
            if (!InventoryUtil.canStack(current, stack = (ItemStack)ListenerTick.mc.player.inventoryContainer.getInventory().get(i))) continue;
            if (stack.getCount() > maxDiff) {
                if (stack.getCount() <= maxSize) continue;
                maxIndex = i;
                maxSize = stack.getCount();
                continue;
            }
            if (stack.getCount() <= limitSize) continue;
            result = i;
            limitSize = stack.getCount();
        }
        if (maxIndex != -1 && (result == -1 || ((ItemStack)ListenerTick.mc.player.inventoryContainer.getInventory().get(result)).getCount() < minSize)) {
            return maxIndex + 100;
        }
        return result;
    }
}

