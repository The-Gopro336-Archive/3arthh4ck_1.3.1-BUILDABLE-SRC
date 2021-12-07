package me.earth.earthhack.impl.modules.combat.autoarmor.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.modes.ArmorMode;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.LevelStack;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

final class ArmorMode$1
extends ArmorMode {
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
            if (!(stack = ArmorMode$1.getStack(i)).isEmpty() && stack.getItem() instanceof ItemArmor && AutoArmor.curseCheck((ItemStack)stack, curse)) {
                float d = DamageUtil.getDamage((ItemStack)stack);
                ItemArmor armor = (ItemArmor)((Object)stack.getItem());
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
                if (map.get(slot) != null || (e2 = (LevelStack)blast.get(slot)) == null) continue;
                map.put(slot, e2);
            }
            map.keySet().retainAll(empty);
            map.forEach((key, value) -> result.put((EntityEquipmentSlot)key, value.getSlot()));
        } else {
            boolean foundBlast = false;
            ArrayList<EntityEquipmentSlot> both = new ArrayList<EntityEquipmentSlot>(4);
            for (EntityEquipmentSlot slot : empty) {
                LevelStack b = (LevelStack)blast.get(slot);
                LevelStack p = (LevelStack)map.get(slot);
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
                    result.put(b, ((LevelStack)map.get(b)).getSlot());
                    continue;
                }
                foundBlast = true;
                result.put(b, ((LevelStack)blast.get(b)).getSlot());
            }
            if (!foundBlast && !blast.isEmpty()) {
                Optional<Map.Entry> first = blast.entrySet().stream().filter(e -> !cursed.contains(e.getKey())).findFirst();
                first.ifPresent(e -> result.put((EntityEquipmentSlot)e.getKey(), ((LevelStack)e.getValue()).getSlot()));
            }
        }
        return result;
    }
}
