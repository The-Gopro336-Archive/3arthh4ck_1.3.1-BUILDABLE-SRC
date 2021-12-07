/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.step.StepESP;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

final class ListenerRender
extends ModuleListener<Step, Render3DEvent> {
    public ListenerRender(Step module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        StepESP esp = ((Step)this.module).esp.getValue();
        if (esp != StepESP.None) {
            BlockPos pos = PositionUtil.getPosition((Entity)ListenerRender.mc.player, 1.0);
            BlockPos up2 = pos.up(2);
            if (ListenerRender.mc.world.getBlockState(up2).getMaterial().blocksMovement()) {
                if (esp == StepESP.Good) {
                    return;
                }
                ((Step)this.module).renderPos(up2);
            }
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos off = pos.offset(facing);
                if (!ListenerRender.mc.world.getBlockState(off).getMaterial().blocksMovement()) continue;
                IBlockState state = ListenerRender.mc.world.getBlockState(off = off.up());
                if (state.getMaterial().blocksMovement() && state.getBoundingBox((IBlockAccess)ListenerRender.mc.world, off) == Block.FULL_BLOCK_AABB) {
                    if (esp != StepESP.Bad) continue;
                    ((Step)this.module).renderPos(off);
                    continue;
                }
                IBlockState up = ListenerRender.mc.world.getBlockState(off.up());
                if (up.getMaterial().blocksMovement()) {
                    if (esp != StepESP.Bad) continue;
                    ((Step)this.module).renderPos(off);
                    continue;
                }
                if (esp != StepESP.Good) continue;
                ((Step)this.module).renderPos(off);
            }
        }
    }
}

