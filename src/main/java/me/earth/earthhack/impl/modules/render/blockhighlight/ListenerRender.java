/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

final class ListenerRender
extends ModuleListener<BlockHighlight, Render3DEvent> {
    private static final ModuleCache<Speedmine> SPEED_MINE = Caches.getModule(Speedmine.class);

    public ListenerRender(BlockHighlight module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (ListenerRender.mc.objectMouseOver != null && ListenerRender.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState state;
            BlockPos pos = ListenerRender.mc.objectMouseOver.getBlockPos();
            if (!(!ListenerRender.mc.world.getWorldBorder().contains(pos) || SPEED_MINE.isEnabled() && pos.equals((Object)((Speedmine)SPEED_MINE.get()).getPos()) || (state = ListenerRender.mc.world.getBlockState(pos)).getMaterial() == Material.AIR)) {
                AxisAlignedBB bb = Interpolation.interpolateAxis(state.getSelectedBoundingBox((World)ListenerRender.mc.world, pos).grow((double)0.002f));
                ((BlockHighlight)this.module).renderInterpAxis(bb);
            }
        }
    }
}

