/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.init.Enchantments
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemElytra
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.combat.autoarmor.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.LevelStack;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

public enum ArmorMode implements Globals
{
    Blast{

        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry, boolean curse, boolean prio, float threshold) {
            boolean wearingBlast = false;
            HashSet<EntityEquipmentSlot> cursed = new HashSet<EntityEquipmentSlot>(6);
            ArrayList<EntityEquipmentSlot> empty = new ArrayList<EntityEquipmentSlot>(4);
            for (int i = 5; i < 9; ++i) {
                ItemStack stack = InventoryUtil.get(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemArmor) {
                        int lvl = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.BLAST_PROTECTION, (ItemStack)stack);
                        if (lvl > 0) {
                            wearingBlast = true;
                        }
                    } else {
                        empty.add(AutoArmor.fromSlot(i));
                    }
                    if (!EnchantmentHelper.hasBindingCurse((ItemStack)stack)) continue;
                    cursed.add(AutoArmor.fromSlot(i));
                    continue;
                }
                empty.add(AutoArmor.fromSlot(i));
            }
            if (wearingBlast && empty.isEmpty()) {
                return new HashMap<EntityEquipmentSlot, Integer>(1, 1.0f);
            }
            HashMap<EntityEquipmentSlot, Object> map = new HashMap<EntityEquipmentSlot, Object>(6);
            HashMap blast = new HashMap(6);
            for (int i = 8; i < 45; ++i) {
                Object stack;
                if (i == 5) {
                    i = 9;
                }
                if (!(stack = 1.getStack(i)).isEmpty() && stack.getItem() instanceof ItemArmor && AutoArmor.curseCheck((ItemStack)stack, curse)) {
                    float d = DamageUtil.getDamage((ItemStack)stack);
                    ItemArmor armor = (ItemArmor)stack.getItem();
                    EntityEquipmentSlot type = armor.getEquipmentSlot();
                    int blastLvL = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.BLAST_PROTECTION, (ItemStack)stack);
                    if (blastLvL != 0) {
                        ArmorMode.compute((ItemStack)stack, blast, type, i, blastLvL, d, prio, threshold);
                    }
                    int lvl = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)stack);
                    if (blastLvL != 0) {
                        if (lvl < 4) continue;
                        lvl += blastLvL;
                    }
                    ArmorMode.compute((ItemStack)stack, map, type, i, lvl, d, prio, threshold);
                }
                if (i != 8 || !xCarry) continue;
                i = 0;
            }
            HashMap<EntityEquipmentSlot, Integer> result = new HashMap<EntityEquipmentSlot, Integer>(6);
            if (wearingBlast) {
                for (EntityEquipmentSlot slot : empty) {
                    Object e2;
                    if (map.get((Object)slot) != null || (e2 = (LevelStack)blast.get((Object)slot)) == null) continue;
                    map.put(slot, e2);
                }
                map.keySet().retainAll(empty);
                map.forEach((key, value) -> result.put((EntityEquipmentSlot)key, value.getSlot()));
            } else {
                boolean foundBlast = false;
                ArrayList<EntityEquipmentSlot> both = new ArrayList<EntityEquipmentSlot>(4);
                for (EntityEquipmentSlot slot : empty) {
                    LevelStack b = (LevelStack)blast.get((Object)slot);
                    LevelStack p = (LevelStack)map.get((Object)slot);
                    if (b == null && p != null) {
                        result.put(slot, p.getSlot());
                        continue;
                    }
                    if (b != null && p == null) {
                        foundBlast = true;
                        result.put(slot, b.getSlot());
                        continue;
                    }
                    if (b == null) continue;
                    both.add(slot);
                }
                for (EntityEquipmentSlot b : both) {
                    if (foundBlast) {
                        result.put(b, ((LevelStack)map.get((Object)b)).getSlot());
                        continue;
                    }
                    foundBlast = true;
                    result.put(b, ((LevelStack)blast.get((Object)b)).getSlot());
                }
                if (!foundBlast && !blast.isEmpty()) {
                    Optional<Map.Entry> first = blast.entrySet().stream().filter(e -> !cursed.contains(e.getKey())).findFirst();
                    first.ifPresent(e -> result.put((EntityEquipmentSlot)e.getKey(), ((LevelStack)e.getValue()).getSlot()));
                }
            }
            return result;
        }
    }
    ,
    Protection{

        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry, boolean curse, boolean prio, float threshold) {
            ArrayList<EntityEquipmentSlot> semi = new ArrayList<EntityEquipmentSlot>(4);
            ArrayList<EntityEquipmentSlot> empty = new ArrayList<EntityEquipmentSlot>(4);
            for (int i = 4; i < 9; ++i) {
                ItemStack stack = InventoryUtil.get(i);
                EntityEquipmentSlot slot = AutoArmor.fromSlot(i);
                if (!stack.isEmpty()) {
                    if (EnchantmentHelper.hasBindingCurse((ItemStack)stack)) continue;
                    if (stack.getItem() instanceof ItemArmor) {
                        if (EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)stack) != 0) continue;
                        semi.add(slot);
                        continue;
                    }
                    empty.add(slot);
                    continue;
                }
                empty.add(slot);
            }
            if (empty.isEmpty()) {
                return new HashMap<EntityEquipmentSlot, Integer>(0);
            }
            HashMap<EntityEquipmentSlot, LevelStack> map = new HashMap<EntityEquipmentSlot, LevelStack>(6);
            for (int i = 8; i < 45; ++i) {
                ItemStack stack;
                if (i == 5) {
                    i = 9;
                }
                if (!(stack = 2.getStack(i)).isEmpty() && stack.getItem() instanceof ItemArmor && AutoArmor.curseCheck(stack, curse)) {
                    float d = DamageUtil.getDamage(stack);
                    ItemArmor armor = (ItemArmor)stack.getItem();
                    EntityEquipmentSlot type = armor.getEquipmentSlot();
                    int lvl = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)stack);
                    if (lvl >= 4) {
                        lvl += EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.BLAST_PROTECTION, (ItemStack)stack);
                    }
                    ArmorMode.compute(stack, map, type, i, lvl, d, prio, threshold);
                }
                if (i != 8 || !xCarry) continue;
                i = 0;
            }
            for (EntityEquipmentSlot s : semi) {
                LevelStack entry = (LevelStack)map.get((Object)s);
                if (entry == null || entry.getLevel() <= 0) continue;
                empty.add(s);
            }
            map.keySet().retainAll(empty);
            HashMap<EntityEquipmentSlot, Integer> result = new HashMap<EntityEquipmentSlot, Integer>(6);
            map.forEach((key, value) -> result.put((EntityEquipmentSlot)key, value.getSlot()));
            return result;
        }
    }
    ,
    Elytra{

        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry, boolean curse, boolean prio, float threshold) {
            Map<EntityEquipmentSlot, Integer> map = Blast.setup(xCarry, curse, prio, threshold);
            int bestDura = 0;
            int bestElytra = -1;
            ItemStack elytra = InventoryUtil.get(6);
            if (!elytra.isEmpty() && (elytra.getItem() instanceof ItemElytra || EnchantmentHelper.hasBindingCurse((ItemStack)elytra))) {
                map.remove((Object)EntityEquipmentSlot.CHEST);
                return map;
            }
            for (int i = 8; i < 45; ++i) {
                ItemStack stack;
                if (i == 5) {
                    i = 9;
                }
                if (!(stack = 3.getStack(i)).isEmpty() && stack.getItem() instanceof ItemElytra && AutoArmor.curseCheck(stack, curse)) {
                    int lvl = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.UNBREAKING, (ItemStack)stack) + 1;
                    int dura = DamageUtil.getDamage(stack) * lvl;
                    if (bestElytra == -1 || !prio && dura > bestDura || prio && (float)dura > threshold && dura < bestDura) {
                        bestElytra = i;
                        bestDura = dura;
                    }
                }
                if (i != 8 || !xCarry) continue;
                i = 0;
            }
            if (bestElytra != -1) {
                map.put(EntityEquipmentSlot.CHEST, bestElytra);
            }
            return map;
        }
    };


    public abstract Map<EntityEquipmentSlot, Integer> setup(boolean var1, boolean var2, boolean var3, float var4);

    public static ItemStack getStack(int slot) {
        if (slot == 8) {
            return ArmorMode.mc.player.inventory.getItemStack();
        }
        return InventoryUtil.get(slot);
    }

    private static void compute(ItemStack stack, Map<EntityEquipmentSlot, LevelStack> map, EntityEquipmentSlot type, int slot, int level, float damage, boolean prio, float threshold) {
        map.compute(type, (k, v) -> {
            if (v == null || !v.isBetter(damage, threshold, level, prio)) {
                return new LevelStack(stack, damage, slot, level);
            }
            return v;
        });
    }
}

