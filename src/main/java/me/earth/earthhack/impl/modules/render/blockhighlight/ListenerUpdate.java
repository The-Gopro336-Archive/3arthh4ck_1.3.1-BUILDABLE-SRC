/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class ListenerUpdate
extends ModuleListener<BlockHighlight, UpdateEvent> {
    public ListenerUpdate(BlockHighlight module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (ListenerUpdate.mc.objectMouseOver != null) {
            switch (ListenerUpdate.mc.objectMouseOver.typeOfHit) {
                case BLOCK: {
                    IBlockState state;
                    BlockPos pos = ListenerUpdate.mc.objectMouseOver.getBlockPos();
                    if (!ListenerUpdate.mc.world.getWorldBorder().contains(pos) || (state = ListenerUpdate.mc.world.getBlockState(pos)).getMaterial() == Material.AIR) break;
                    ItemStack stack = state.getBlock().getItem((World)ListenerUpdate.mc.world, pos, state);
                    ((BlockHighlight)this.module).current = stack.getItem().getItemStackDisplayName(stack);
                    return;
                }
                case ENTITY: {
                    Entity entity = ListenerUpdate.mc.objectMouseOver.entityHit;
                    if (entity == null) break;
                    ((BlockHighlight)this.module).current = EntityNames.getName(entity);
                    return;
                }
            }
        }
        ((BlockHighlight)this.module).current = null;
    }
}

