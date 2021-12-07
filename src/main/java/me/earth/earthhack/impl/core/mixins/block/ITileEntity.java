/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.tileentity.TileEntity
 */
package me.earth.earthhack.impl.core.mixins.block;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={TileEntity.class})
public interface ITileEntity {
    @Accessor(value="blockType")
    public Block getBlockType();

    @Accessor(value="blockType")
    public void setBlockType(Block var1);
}

