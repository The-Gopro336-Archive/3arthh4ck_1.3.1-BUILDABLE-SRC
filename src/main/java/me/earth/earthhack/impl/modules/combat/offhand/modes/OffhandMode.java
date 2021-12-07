/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 */
package me.earth.earthhack.impl.modules.combat.offhand.modes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class OffhandMode {
    public static final OffhandMode TOTEM = new OffhandMode(Items.TOTEM_OF_UNDYING, "Totem");
    public static final OffhandMode GAPPLE = new OffhandMode(Items.GOLDEN_APPLE, "Gapple");
    public static final OffhandMode CRYSTAL = new OffhandMode(Items.END_CRYSTAL, "Crystal");
    public static final OffhandMode OBSIDIAN = new OffhandMode(Blocks.OBSIDIAN, "Obsidian");
    private final String name;
    private final Item item;

    public OffhandMode(Block block, String name) {
        this(Item.getItemFromBlock((Block)block), name);
    }

    public OffhandMode(Item item, String name) {
        this.item = item;
        this.name = name;
    }

    public Item getItem() {
        return this.item;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.item == null ? 0 : this.item.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof OffhandMode) {
            return ((OffhandMode)o).item == this.item;
        }
        return false;
    }
}

