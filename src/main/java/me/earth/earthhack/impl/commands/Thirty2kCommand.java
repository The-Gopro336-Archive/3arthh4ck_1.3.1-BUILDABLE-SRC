/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Thirty2kCommand
extends AbstractStackCommand {
    public Thirty2kCommand() {
        super("32k", "32k");
        CommandDescriptions.register(this, "Gives you a 32k sword.");
    }

    @Override
    protected ItemStack getStack(String[] args) {
        ItemStack s = new ItemStack(Items.DIAMOND_SWORD);
        s.setStackDisplayName("3\u00b2arthbl4de");
        s.setCount(64);
        EnchantmentUtil.addEnchantment(s, 16, 32767);
        EnchantmentUtil.addEnchantment(s, 19, 10);
        EnchantmentUtil.addEnchantment(s, 20, 32767);
        EnchantmentUtil.addEnchantment(s, 21, 10);
        EnchantmentUtil.addEnchantment(s, 22, 3);
        EnchantmentUtil.addEnchantment(s, 34, 32767);
        EnchantmentUtil.addEnchantment(s, 70, 1);
        EnchantmentUtil.addEnchantment(s, 71, 1);
        return s;
    }
}

