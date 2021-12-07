package me.earth.earthhack.impl.modules.combat.autoarmor.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

final class ArmorMode$2
extends ArmorMode {
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
            if (!(stack = ArmorMode$2.getStack(i)).isEmpty() && stack.getItem() instanceof ItemArmor && AutoArmor.curseCheck(stack, curse)) {
                float d = DamageUtil.getDamage(stack);
                ItemArmor armor = (ItemArmor)((Object)stack.getItem());
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
            LevelStack entry = (LevelStack)map.get(s);
            if (entry == null || entry.getLevel() <= 0) continue;
            empty.add(s);
        }
        map.keySet().retainAll(empty);
        HashMap<EntityEquipmentSlot, Integer> result = new HashMap<EntityEquipmentSlot, Integer>(6);
        map.forEach((key, value) -> result.put((EntityEquipmentSlot)key, value.getSlot()));
        return result;
    }
}
