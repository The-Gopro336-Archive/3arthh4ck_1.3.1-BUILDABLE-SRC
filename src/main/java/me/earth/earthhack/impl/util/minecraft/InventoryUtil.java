package me.earth.earthhack.impl.util.minecraft;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

public class InventoryUtil
implements Globals {
    public static final ItemStack ILLEGAL_STACK = new ItemStack(Item.getItemFromBlock((Block)Blocks.BEDROCK));

    public static void switchTo(int slot) {
        if (InventoryUtil.mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            InventoryUtil.mc.player.inventory.currentItem = slot;
            InventoryUtil.syncItem();
        }
    }

    public static void switchToBypass(int slot) {
        if (InventoryUtil.mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                int lastSlot = InventoryUtil.mc.player.inventory.currentItem;
                int targetSlot = InventoryUtil.hotbarToInventory(slot);
                int currentSlot = InventoryUtil.hotbarToInventory(lastSlot);
                InventoryUtil.mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
                InventoryUtil.mc.playerController.windowClick(0, currentSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
                InventoryUtil.mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
            });
        }
    }

    public static void switchToBypassAlt(int slot) {
        if (slot != -1) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> InventoryUtil.mc.playerController.windowClick(0, slot, InventoryUtil.mc.player.inventory.currentItem, ClickType.SWAP, InventoryUtil.mc.player));
        }
    }

    public static void bypassSwitch(int slot) {
        if (slot >= 0) {
            InventoryUtil.mc.playerController.pickItem(slot);
        }
    }

    public static void illegalSync() {
        if (InventoryUtil.mc.player != null) {
            PacketUtil.click(0, 0, 0, ClickType.PICKUP, ILLEGAL_STACK, (short)0);
        }
    }

    public static int findHotbarBlock(Block block, Block ... optional) {
        return InventoryUtil.findInHotbar((ItemStack s) -> ItemUtil.areSame(s, block), CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findHotbarItem(Item item, Item ... optional) {
        return InventoryUtil.findInHotbar((ItemStack s) -> ItemUtil.areSame(s, item), CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findInHotbar(Predicate<ItemStack> condition) {
        return InventoryUtil.findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition, boolean offhand) {
        if (offhand && condition.test(InventoryUtil.mc.player.getHeldItemOffhand())) {
            return -2;
        }
        int result = -1;
        for (int i = 8; i > -1; --i) {
            if (!condition.test(InventoryUtil.mc.player.inventory.getStackInSlot(i))) continue;
            result = i;
            if (InventoryUtil.mc.player.inventory.currentItem == i) break;
        }
        return result;
    }

    public static int findInInventory(Predicate<ItemStack> condition, boolean xCarry) {
        ItemStack stack;
        int i;
        for (i = 9; i < 45; ++i) {
            stack = (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
            if (!condition.test(stack)) continue;
            return i;
        }
        if (xCarry) {
            for (i = 1; i < 5; ++i) {
                stack = (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
                if (!condition.test(stack)) continue;
                return i;
            }
        }
        return -1;
    }

    public static int findInCraftingTable(Container container, Predicate<ItemStack> condition) {
        for (int i = 11; i < 47; ++i) {
            ItemStack stack = (ItemStack)container.getInventory().get(i);
            if (!condition.test(stack)) continue;
            return i;
        }
        return -1;
    }

    public static int findInHotbar(Predicate<ItemStack> condition, Iterable<Predicate<ItemStack>> optional) {
        int result;
        block1: {
            Predicate<ItemStack> opt;
            result = InventoryUtil.findInHotbar(condition);
            if (result != -1) break block1;
            Iterator<Predicate<ItemStack>> iterator = optional.iterator();
            while (iterator.hasNext() && (result = InventoryUtil.findInHotbar(opt = iterator.next())) == -1) {
            }
        }
        return result;
    }

    public static int findBlock(Block block, boolean xCarry) {
        ItemStack stack;
        int i;
        if (ItemUtil.areSame(InventoryUtil.mc.player.inventory.getItemStack(), block)) {
            return -2;
        }
        for (i = 9; i < 45; ++i) {
            stack = (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
            if (!ItemUtil.areSame(stack, block)) continue;
            return i;
        }
        if (xCarry) {
            for (i = 1; i < 5; ++i) {
                stack = (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
                if (!ItemUtil.areSame(stack, block)) continue;
                return i;
            }
        }
        return -1;
    }

    public static int findItem(Item item, Container container) {
        for (int i = 0; i < container.getInventory().size(); ++i) {
            ItemStack stack = (ItemStack)container.getInventory().get(i);
            if (stack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int findItem(Item item, boolean xCarry) {
        return InventoryUtil.findItem(item, xCarry, Collections.emptySet());
    }

    public static int findItem(Item item, boolean xCarry, Set<Integer> ignore) {
        int i;
        if (InventoryUtil.mc.player.inventory.getItemStack().getItem() == item && !ignore.contains(-2)) {
            return -2;
        }
        for (i = 9; i < 45; ++i) {
            if (ignore.contains(i) || InventoryUtil.get(i).getItem() != item) continue;
            return i;
        }
        if (xCarry) {
            for (i = 1; i < 5; ++i) {
                if (ignore.contains(i) || InventoryUtil.get(i).getItem() != item) continue;
                return i;
            }
        }
        return -1;
    }

    public static int getCount(Item item) {
        int result = 0;
        for (int i = 0; i < 46; ++i) {
            ItemStack stack = (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() != item) continue;
            result += stack.getCount();
        }
        if (InventoryUtil.mc.player.inventory.getItemStack().getItem() == item) {
            result += InventoryUtil.mc.player.inventory.getItemStack().getCount();
        }
        return result;
    }

    public static boolean isHoldingServer(Item item) {
        ItemStack offHand = InventoryUtil.mc.player.getHeldItemOffhand();
        if (ItemUtil.areSame(offHand, item)) {
            return true;
        }
        ItemStack mainHand = InventoryUtil.mc.player.getHeldItemMainhand();
        if (ItemUtil.areSame(mainHand, item)) {
            int current = InventoryUtil.mc.player.inventory.currentItem;
            int server = InventoryUtil.getServerItem();
            return server == current;
        }
        return false;
    }

    public static boolean isHolding(Class<?> clazz) {
        return clazz.isAssignableFrom(InventoryUtil.mc.player.getHeldItemMainhand().getItem().getClass()) || clazz.isAssignableFrom(InventoryUtil.mc.player.getHeldItemOffhand().getItem().getClass());
    }

    public static boolean isHolding(Item item) {
        return InventoryUtil.isHolding((EntityLivingBase)InventoryUtil.mc.player, item);
    }

    public static boolean isHolding(Block block) {
        return InventoryUtil.isHolding((EntityLivingBase)InventoryUtil.mc.player, block);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();
        return ItemUtil.areSame(mainHand, item) || ItemUtil.areSame(offHand, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Block block) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();
        return ItemUtil.areSame(mainHand, block) || ItemUtil.areSame(offHand, block);
    }

    public static EnumHand getHand(int slot) {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static EnumHand getHand(Item item) {
        return InventoryUtil.mc.player.getHeldItemMainhand().getItem() == item ? EnumHand.MAIN_HAND : (InventoryUtil.mc.player.getHeldItemOffhand().getItem() == item ? EnumHand.OFF_HAND : null);
    }

    public static boolean validScreen() {
        return !(InventoryUtil.mc.currentScreen instanceof GuiContainer) || InventoryUtil.mc.currentScreen instanceof GuiInventory;
    }

    public static int getServerItem() {
        return ((IPlayerControllerMP)((Object)InventoryUtil.mc.playerController)).getItem();
    }

    public static void syncItem() {
        ((IPlayerControllerMP)((Object)InventoryUtil.mc.playerController)).syncItem();
    }

    public static void click(int slot) {
        InventoryUtil.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
    }

    public static ItemStack get(int slot) {
        if (slot == -2) {
            return InventoryUtil.mc.player.inventory.getItemStack();
        }
        return (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static NonNullList<ItemStack> getInventory() {
        return InventoryUtil.mc.player.inventoryContainer.getInventory();
    }

    public static void put(int slot, ItemStack stack) {
        if (slot == -2) {
            InventoryUtil.mc.player.inventory.setItemStack(stack);
        }
        InventoryUtil.mc.player.inventoryContainer.putStackInSlot(slot, stack);
    }

    public static int findEmptyHotbarSlot() {
        int result = -1;
        for (int i = 8; i > -1; --i) {
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() != Items.AIR) continue;
            result = i;
        }
        return result;
    }

    public static int hotbarToInventory(int slot) {
        if (slot == -2) {
            return 45;
        }
        if (slot > -1 && slot < 9) {
            return 36 + slot;
        }
        return slot;
    }

    public static boolean canStack(ItemStack inSlot, ItemStack stack) {
        return inSlot.isEmpty() || inSlot.getItem() == stack.getItem() && inSlot.getMaxStackSize() > 1 && (!inSlot.getHasSubtypes() || inSlot.getMetadata() == stack.getMetadata()) && ItemStack.areItemStackTagsEqual((ItemStack)inSlot, (ItemStack)stack);
    }

    public static boolean equals(ItemStack stack1, ItemStack stack2) {
        boolean empty2;
        if (stack1 == null) {
            return stack2 == null;
        }
        if (stack2 == null) {
            return false;
        }
        boolean empty1 = stack1.isEmpty();
        return empty1 == (empty2 = stack2.isEmpty()) && stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItem() == stack1.getItem() && stack1.getHasSubtypes() == stack2.getHasSubtypes() && stack1.getMetadata() == stack2.getMetadata() && ItemStack.areItemStackTagsEqual((ItemStack)stack1, (ItemStack)stack2);
    }

    public static void clickLocked(int slot, int to, Item inSlot, Item inTo) {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if ((slot == -1 || InventoryUtil.get(slot).getItem() == inSlot) && InventoryUtil.get(to).getItem() == inTo) {
                boolean multi;
                boolean bl = multi = slot >= 0;
                if (multi) {
                    Managers.NCP.startMultiClick();
                    InventoryUtil.click(slot);
                }
                InventoryUtil.click(to);
                if (multi) {
                    Managers.NCP.releaseMultiClick();
                }
            }
        });
    }
}
