package me.earth.earthhack.impl.modules.combat.autoarmor.modes;

import java.util.Map;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.modes.ArmorMode;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

final class ArmorMode$3
extends ArmorMode {
    @Override
    public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry, boolean curse, boolean prio, float threshold) {
        Map<EntityEquipmentSlot, Integer> map = Blast.setup(xCarry, curse, prio, threshold);
        int bestDura = 0;
        int bestElytra = -1;
        ItemStack elytra = InventoryUtil.get(6);
        if (!elytra.isEmpty() && (elytra.getItem() instanceof ItemElytra || EnchantmentHelper.hasBindingCurse((ItemStack)elytra))) {
            map.remove(EntityEquipmentSlot.CHEST);
            return map;
        }
        for (int i = 8; i < 45; ++i) {
            ItemStack stack;
            if (i == 5) {
                i = 9;
            }
            if (!(stack = ArmorMode$3.getStack(i)).isEmpty() && stack.getItem() instanceof ItemElytra && AutoArmor.curseCheck(stack, curse)) {
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
}
