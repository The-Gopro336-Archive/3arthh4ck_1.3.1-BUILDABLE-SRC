/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemFood
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.misc.nuker;

import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<Nuker, MotionUpdateEvent> {
    public ListenerMotion(Nuker module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            Set<Block> blocks;
            if (((Nuker)this.module).nuke.getValue().booleanValue() && ((Nuker)this.module).currentSelection != null) {
                ((Nuker)this.module).currentSelection.removeIf(pos -> !MineUtil.canBreak(pos) || BlockUtil.getDistanceSqDigging(pos) > (double)MathUtil.square(((Nuker)this.module).range.getValue().floatValue()));
                if (((Nuker)this.module).timer.passed(((Nuker)this.module).timeout.getValue().intValue()) && ((Nuker)this.module).actions.isEmpty()) {
                    ((Nuker)this.module).breakSelection(((Nuker)this.module).currentSelection, ((Nuker)this.module).autoTool.getValue());
                }
            }
            if (!(blocks = ((Nuker)this.module).getBlocks()).isEmpty() && ((Nuker)this.module).timer.passed(((Nuker)this.module).timeout.getValue().intValue()) && ((Nuker)this.module).actions.isEmpty()) {
                if (ListenerMotion.mc.player.getActiveItemStack().getItem() instanceof ItemFood) {
                    return;
                }
                HashSet<BlockPos> toAttack = new HashSet<BlockPos>();
                BlockPos middle = PositionUtil.getPosition();
                int maxRadius = Sphere.getRadius(((Nuker)this.module).range.getValue().floatValue());
                for (int i = 1; i < maxRadius; ++i) {
                    BlockPos pos2 = middle.add(Sphere.get(i));
                    if (BlockUtil.getDistanceSq(pos2) > (double)MathUtil.square(((Nuker)this.module).range.getValue().floatValue()) || !blocks.contains((Object)ListenerMotion.mc.world.getBlockState(pos2).getBlock())) continue;
                    toAttack.add(pos2);
                    if (((Nuker)this.module).rotate.getValue() == Rotate.Normal) break;
                }
                ((Nuker)this.module).breakSelection(toAttack, true);
            }
            if (((Nuker)this.module).rotations != null) {
                event.setYaw(((Nuker)this.module).rotations[0]);
                event.setPitch(((Nuker)this.module).rotations[1]);
            }
            ((Nuker)this.module).rotations = null;
        } else {
            ((Nuker)this.module).lastSlot = -1;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> CollectionUtil.emptyQueue(((Nuker)this.module).actions));
            ((Nuker)this.module).breaking = false;
        }
    }
}

