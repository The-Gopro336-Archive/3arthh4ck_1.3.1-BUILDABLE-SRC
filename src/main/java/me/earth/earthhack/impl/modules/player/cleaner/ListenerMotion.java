/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.cleaner;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.modules.player.cleaner.Cleaner;
import me.earth.earthhack.impl.modules.player.cleaner.ItemToDrop;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingInteger;
import me.earth.earthhack.impl.modules.player.cleaner.SettingMap;
import me.earth.earthhack.impl.modules.player.cleaner.SlotCount;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<Cleaner, MotionUpdateEvent> {
    private static final ModuleCache<AutoArmor> AUTO_ARMOR = Caches.getModule(AutoArmor.class);

    public ListenerMotion(Cleaner module) {
        super(module, MotionUpdateEvent.class, 1000000);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        block15: {
            block12: {
                WindowClick action;
                block14: {
                    HashMap<Item, ItemToDrop> items;
                    block13: {
                        int i;
                        if (((Cleaner)this.module).action == null && !((Cleaner)this.module).timer.passed(((Cleaner)this.module).delay.getValue().intValue()) || !Managers.NCP.getClickTimer().passed(((Cleaner)this.module).globalDelay.getValue().intValue()) || ListenerMotion.mc.player.isCreative() || !InventoryUtil.validScreen() || AUTO_ARMOR.returnIfPresent(AutoArmor::isActive, false) != false || ((Cleaner)this.module).inInventory.getValue() == false && ListenerMotion.mc.currentScreen instanceof GuiInventory || !((Cleaner)this.module).cleanInLoot.getValue().booleanValue() && !ListenerMotion.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox()).isEmpty() && (!((Cleaner)this.module).cleanWithFull.getValue().booleanValue() || !this.isInvFull())) {
                            return;
                        }
                        if (event.getStage() != Stage.PRE) break block12;
                        if (((Cleaner)this.module).stack.getValue().booleanValue() && this.stack() || ((Cleaner)this.module).xCarry.getValue().booleanValue() && this.doXCarry()) {
                            return;
                        }
                        items = new HashMap<Item, ItemToDrop>();
                        boolean prio = ((Cleaner)this.module).prioHotbar.getValue();
                        Item drag = null;
                        ItemStack draggedStack = ListenerMotion.mc.player.inventory.getItemStack();
                        if (this.check(draggedStack, -2, items)) {
                            drag = draggedStack.getItem();
                        } else if (!draggedStack.isEmpty()) {
                            return;
                        }
                        int n = i = prio ? 44 : 9;
                        while (prio ? i > 8 : i <= 44) {
                            ItemStack stack = InventoryUtil.get(i);
                            this.check(stack, i, items);
                            i = prio ? --i : ++i;
                        }
                        action = null;
                        if (drag == null) break block13;
                        ItemToDrop dragged = (ItemToDrop)items.get((Object)drag);
                        if (dragged == null || !dragged.shouldDrop()) break block14;
                        action = new WindowClick(-999, ItemStack.EMPTY, ListenerMotion.mc.player.inventory.getItemStack());
                        break block14;
                    }
                    for (ItemToDrop toDrop : items.values()) {
                        if (!toDrop.shouldDrop()) continue;
                        int s = toDrop.getSlot();
                        action = new WindowClick(-1, ItemStack.EMPTY, s, InventoryUtil.get(s), -1, p -> p.windowClick(0, s, 1, ClickType.THROW, (EntityPlayer)ListenerMotion.mc.player));
                        break;
                    }
                }
                if (action != null) {
                    if (((Cleaner)this.module).rotate.getValue().booleanValue()) {
                        if (MovementUtil.isMoving()) {
                            event.setYaw(event.getYaw() - 180.0f);
                        } else {
                            event.setYaw(this.getYaw(event.getYaw()));
                        }
                        event.setPitch(-5.0f);
                        ((Cleaner)this.module).action = action;
                    } else {
                        ((Cleaner)this.module).action = action;
                        ((Cleaner)this.module).runAction();
                    }
                }
                break block15;
            }
            ((Cleaner)this.module).runAction();
        }
    }

    private boolean stack() {
        ItemStack drag = ListenerMotion.mc.player.inventory.getItemStack();
        if (drag.isEmpty()) {
            Object map;
            HashMap<Item, Object> pref = new HashMap<Item, Object>();
            HashMap corresponding = new HashMap();
            for (int i = 44; i > 8; --i) {
                ItemStack stack = InventoryUtil.get(i);
                if (stack.isEmpty()) continue;
                Item item = stack.getItem();
                Map map2 = (Map)corresponding.get((Object)item);
                if (map2 != null) {
                    if (stack.getCount() >= stack.getMaxStackSize() || map2.containsKey(stack.getCount())) continue;
                    map2.put(i, stack.getCount());
                    continue;
                }
                map = (SettingMap)pref.get((Object)item);
                if (map == null) {
                    Setting<Integer> setting = this.getSetting(stack);
                    if (setting == null) {
                        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                        if (stack.getCount() != stack.getMaxStackSize()) {
                            hashMap.put(i, stack.getCount());
                        }
                        corresponding.put(item, hashMap);
                        continue;
                    }
                    map = new SettingMap(setting, new HashMap<Integer, Integer>());
                    pref.put(stack.getItem(), map);
                }
                ((SettingMap)map).getMap().put(i, stack.getCount());
            }
            TreeMap<Integer, Map.Entry<Integer, Integer>> best = new TreeMap<Integer, Map.Entry<Integer, Integer>>();
            for (Map.Entry entry : pref.entrySet()) {
                SettingMap settingMap = (SettingMap)entry.getValue();
                if (settingMap.getMap().size() < 2 || settingMap.getSetting().getValue() == 0) continue;
                ItemStack deprec = new ItemStack((Item)entry.getKey());
                int max = settingMap.getSetting().getValue() * deprec.getMaxStackSize();
                int s = 0;
                int total = 0;
                int fullStacks = 0;
                for (int stackCount : settingMap.getMap().values()) {
                    if (stackCount == deprec.getMaxStackSize()) {
                        ++fullStacks;
                    }
                    total += stackCount;
                    ++s;
                }
                boolean smart = ((Cleaner)this.module).smartStack.getValue();
                if (total > max && !smart || fullStacks >= settingMap.getSetting().getValue()) continue;
                int m = settingMap.getSetting().getValue();
                Map<Integer, Integer> sMap = CollectionUtil.sortByValue(settingMap.getMap());
                if (!this.findBest(sMap, (Item)entry.getKey(), best, smart, s, m)) continue;
                return true;
            }
            Map.Entry b = best.values().stream().findFirst().orElse(null);
            if (b != null) {
                this.click((Integer)b.getValue(), (Integer)b.getKey());
                return true;
            }
            for (Map.Entry entry : corresponding.entrySet()) {
                Map<Integer, Integer> sort;
                map = (Map)entry.getValue();
                if (map.size() < 2 || !this.findBest(sort = CollectionUtil.sortByValue(map), (Item)entry.getKey(), best, false, 0, 0)) continue;
                return true;
            }
            b = best.values().stream().findFirst().orElse(null);
            if (b != null) {
                this.click((Integer)b.getValue(), (Integer)b.getKey());
                return true;
            }
        } else if (((Cleaner)this.module).stackDrag.getValue().booleanValue()) {
            Setting<Integer> setting = this.getSetting(drag);
            if (setting != null && setting.getValue() == 0) {
                return false;
            }
            for (int i = 44; i > 8; --i) {
                ItemStack stack = InventoryUtil.get(i);
                if (!InventoryUtil.canStack(stack, drag) || stack.getCount() + drag.getCount() > stack.getMaxStackSize()) continue;
                int finalI = i;
                Item item = stack.getItem();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                    if (InventoryUtil.get(finalI).getItem() == item) {
                        InventoryUtil.click(finalI);
                    }
                });
                ((Cleaner)this.module).timer.reset();
                return true;
            }
        }
        return false;
    }

    private boolean findBest(Map<Integer, Integer> map, Item item, Map<Integer, Map.Entry<Integer, Integer>> best, boolean smart, int stacks, int max) {
        HashSet<Integer> checked = new HashSet<Integer>((int)((double)map.size() * 1.5));
        for (Map.Entry<Integer, Integer> inner : map.entrySet()) {
            checked.add(inner.getKey());
            for (Map.Entry<Integer, Integer> sec : map.entrySet()) {
                ItemStack stack2;
                if (checked.contains(sec.getKey())) continue;
                int diff = item.getItemStackLimit();
                if (inner.getValue() == diff || sec.getValue() == diff || (diff -= inner.getValue() + sec.getValue()) < 0 && (!smart || stacks <= max)) continue;
                int i = Math.max(inner.getKey(), sec.getKey());
                int j = Math.min(inner.getKey(), sec.getKey());
                ItemStack stack1 = InventoryUtil.get(i);
                if (!InventoryUtil.canStack(stack1, stack2 = InventoryUtil.get(j))) continue;
                if (diff == 0) {
                    this.click(j, i);
                    return true;
                }
                best.put(diff, new AbstractMap.SimpleEntry<Integer, Integer>(i, j));
            }
        }
        return false;
    }

    private boolean doXCarry() {
        int xCarry = this.getEmptyXCarry();
        ItemStack drag = ListenerMotion.mc.player.inventory.getItemStack();
        if (xCarry == -1 || !drag.isEmpty() && !((Cleaner)this.module).dragCarry.getValue().booleanValue() || this.getSetting(drag) != null) {
            return false;
        }
        int stacks = 0;
        HashSet<Item> invalid = new HashSet<Item>();
        HashMap<Item, List> slots = new HashMap<Item, List>();
        for (int i = 44; i > 8; --i) {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.isEmpty()) continue;
            ++stacks;
            if (invalid.contains((Object)stack.getItem())) continue;
            Setting<Integer> setting = this.getSetting(stack);
            if (setting == null) {
                slots.computeIfAbsent(stack.getItem(), v -> new ArrayList()).add(new SlotCount(stack.getCount(), i));
                continue;
            }
            invalid.add(stack.getItem());
        }
        if (stacks < ((Cleaner)this.module).xCarryStacks.getValue()) {
            return false;
        }
        if (drag.isEmpty()) {
            int best = -1;
            int bestSize = 0;
            for (Map.Entry entry : slots.entrySet()) {
                int size = ((List)entry.getValue()).size();
                ItemStack deprec = new ItemStack((Item)entry.getKey());
                if (size < ((Cleaner)this.module).minXcarry.getValue() || size <= bestSize) continue;
                for (SlotCount count : (List)entry.getValue()) {
                    if (count.getSlot() >= 36 || count.getCount() != deprec.getMaxStackSize()) continue;
                    best = count.getSlot();
                    bestSize = size;
                }
            }
            if (best != -1) {
                this.click(best, xCarry);
                return true;
            }
        } else {
            List counts = (List)slots.get((Object)drag.getItem());
            if (counts == null && ((Cleaner)this.module).minXcarry.getValue() == 0 || counts != null && counts.size() >= ((Cleaner)this.module).minXcarry.getValue()) {
                Item item = InventoryUtil.get(xCarry).getItem();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                    if (InventoryUtil.get(xCarry).getItem() == item) {
                        InventoryUtil.click(xCarry);
                    }
                });
                ((Cleaner)this.module).timer.reset();
                return true;
            }
        }
        return false;
    }

    private int getEmptyXCarry() {
        for (int i = 1; i < 5; ++i) {
            ItemStack stack = InventoryUtil.get(i);
            if (!stack.isEmpty() && stack.getItem() != Items.AIR) continue;
            return i;
        }
        return -1;
    }

    private void click(int first, int second) {
        Item firstItem = InventoryUtil.get(first).getItem();
        Item secondItem = InventoryUtil.get(second).getItem();
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if (InventoryUtil.get(first).getItem() != firstItem || InventoryUtil.get(second).getItem() != secondItem) {
                return;
            }
            Managers.NCP.startMultiClick();
            InventoryUtil.click(first);
            InventoryUtil.click(second);
            Managers.NCP.releaseMultiClick();
        });
        ((Cleaner)this.module).timer.reset();
    }

    private Setting<Integer> getSetting(ItemStack stack) {
        if (!stack.isEmpty() && !((Cleaner)this.module).isStackValid(stack)) {
            Item item = stack.getItem();
            return ((Cleaner)this.module).getSetting(item.getItemStackDisplayName(stack), RemovingInteger.class);
        }
        return null;
    }

    private boolean check(ItemStack stack, int i, Map<Item, ItemToDrop> items) {
        if (!stack.isEmpty() && !((Cleaner)this.module).isStackValid(stack)) {
            Item item = stack.getItem();
            Object setting = ((Cleaner)this.module).getSetting(item.getItemStackDisplayName(stack), RemovingInteger.class);
            items.computeIfAbsent(item, v -> new ItemToDrop((Setting<Integer>)setting)).addSlot(i, stack.getCount());
            return true;
        }
        return false;
    }

    private float getYaw(float yaw) {
        int same = 0;
        int bestCount = 0;
        EnumFacing bestFacing = null;
        BlockPos pos = PositionUtil.getPosition().up();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset;
            int count = 0;
            BlockPos current = pos;
            for (int i = 0; i < 5 && !ListenerMotion.mc.world.getBlockState(offset = current.offset(facing)).getMaterial().blocksMovement(); ++i) {
                ++count;
                current = offset;
            }
            if (count == bestCount || bestFacing == null) {
                ++same;
            }
            if (count <= bestCount) continue;
            bestCount = count;
            bestFacing = facing;
        }
        if (bestFacing == null || same == 4) {
            return yaw - 180.0f;
        }
        return bestFacing.getHorizontalAngle();
    }

    private boolean isInvFull() {
        for (int i = 9; i < 45; ++i) {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.isEmpty()) {
                return false;
            }
            if (stack.getCount() == stack.getMaxStackSize() || !((Cleaner)this.module).sizeCheck.getValue().booleanValue()) continue;
            for (EntityItem entity : ListenerMotion.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox())) {
                if (!InventoryUtil.canStack(stack, entity.getItem())) continue;
                return false;
            }
        }
        return true;
    }
}

