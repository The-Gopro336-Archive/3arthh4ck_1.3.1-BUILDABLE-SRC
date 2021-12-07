/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiChest
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiShulkerBox
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.NonNullList
 */
package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autoregear.AutoRegear;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

final class ListenerTick
extends ModuleListener<AutoRegear, TickEvent> {
    public ListenerTick(AutoRegear module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.currentScreen instanceof GuiShulkerBox) {
            for (int i = 0; i < 36; ++i) {
                int itemId;
                Setting<?> setting;
                if (!((AutoRegear)this.module).delayTimer.passed(((AutoRegear)this.module).delay.getValue().intValue()) || (setting = ((AutoRegear)this.module).getSettingFromSlot(i)) == null || (itemId = Integer.parseInt(setting.getName().split(":")[1])) == 0) continue;
                Item item = Item.getItemById((int)itemId);
                int shulkerSlot = InventoryUtil.findItem(item, ((GuiShulkerBox)ListenerTick.mc.currentScreen).inventorySlots);
                ItemStack stackInSlot = (ItemStack)((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.getInventory().get(i + 27);
                if (stackInSlot.getMaxStackSize() == 1 || stackInSlot.getMaxStackSize() == stackInSlot.getCount() || stackInSlot.getItem() != item && stackInSlot.getItem() != Items.AIR || shulkerSlot == -1 || shulkerSlot > 26) continue;
                ListenerTick.mc.playerController.windowClick(((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.windowId, shulkerSlot, 0, ClickType.PICKUP, (EntityPlayer)ListenerTick.mc.player);
                ListenerTick.mc.playerController.windowClick(((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.windowId, i + 27, 0, ClickType.PICKUP, (EntityPlayer)ListenerTick.mc.player);
                ListenerTick.mc.playerController.windowClick(((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.windowId, shulkerSlot, 0, ClickType.PICKUP, (EntityPlayer)ListenerTick.mc.player);
                ((AutoRegear)this.module).delayTimer.reset();
            }
        } else if (ListenerTick.mc.currentScreen instanceof GuiChest && ((AutoRegear)this.module).shouldRegear && ((AutoRegear)this.module).grabShulker.getValue().booleanValue() && !((AutoRegear)this.module).hasKit()) {
            int emptySlot;
            int slot = -1;
            for (int i = 0; i < 28; ++i) {
                NBTTagCompound blockEntityTag;
                NBTTagCompound tagCompound;
                boolean foundExp = false;
                boolean foundCrystals = false;
                boolean foundGapples = false;
                ItemStack stack2 = (ItemStack)((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.getInventory().get(i);
                if (!(stack2.getItem() instanceof ItemShulkerBox) || (tagCompound = stack2.getTagCompound()) == null || !tagCompound.hasKey("BlockEntityTag", 10) || !(blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) continue;
                NonNullList nonNullList = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
                ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonNullList);
                for (ItemStack stack1 : nonNullList) {
                    if (stack1.getItem() == Items.GOLDEN_APPLE) {
                        foundGapples = true;
                        continue;
                    }
                    if (stack1.getItem() == Items.EXPERIENCE_BOTTLE) {
                        foundExp = true;
                        continue;
                    }
                    if (stack1.getItem() != Items.END_CRYSTAL) continue;
                    foundCrystals = true;
                }
                if (foundExp && foundGapples && foundCrystals) {
                    slot = i;
                    continue;
                }
                if (((AutoRegear)this.module).hasKit() || ((AutoRegear)this.module).getShulkerBox() != null) continue;
                ((AutoRegear)this.module).shouldRegear = false;
                ListenerTick.mc.player.closeScreen();
                return;
            }
            if (slot != -1 && (emptySlot = InventoryUtil.findInInventory(stack -> stack.isEmpty() || stack.getItem() == Items.AIR, false)) != -1) {
                ListenerTick.mc.playerController.windowClick(((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.windowId, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)ListenerTick.mc.player);
            }
            ListenerTick.mc.player.closeScreen();
        }
    }
}

