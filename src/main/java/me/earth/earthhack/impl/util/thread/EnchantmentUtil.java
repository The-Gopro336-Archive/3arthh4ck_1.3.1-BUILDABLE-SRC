/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.DamageSource
 */
package me.earth.earthhack.impl.util.thread;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;

public class EnchantmentUtil {
    public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source) {
        int modifier = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            NBTTagList nbttaglist = stack.getEnchantmentTagList();
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                short id = nbttaglist.getCompoundTagAt(i).getShort("id");
                short lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
                Enchantment ench = Enchantment.getEnchantmentByID((int)id);
                if (ench == null) continue;
                modifier += ench.calcModifierDamage((int)lvl, source);
            }
        }
        return modifier;
    }

    public static void addEnchantment(ItemStack stack, int id, int level) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey("ench", 9)) {
            stack.getTagCompound().setTag("ench", (NBTBase)new NBTTagList());
        }
        NBTTagList nbttaglist = stack.getTagCompound().getTagList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)id);
        nbttagcompound.setShort("lvl", (short)level);
        nbttaglist.appendTag((NBTBase)nbttagcompound);
    }
}

