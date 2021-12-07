package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

final class ListenerObby
extends ObbyListener<HighJump> {
    public ListenerObby(HighJump module, int priority) {
        super(module, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (!((HighJump)this.module).scaffold.getValue().booleanValue() || !ListenerObby.mc.gameSettings.keyBindJump.isKeyDown() || ((HighJump)this.module).motionY < ((HighJump)this.module).scaffoldY.getValue() || ((HighJump)this.module).motionY > ((HighJump)this.module).scaffoldMaxY.getValue() || InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]) == -1) {
            return;
        }
        super.invoke(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        BlockPos p;
        BlockPos pos = PositionUtil.getPosition(RotationUtil.getRotationPlayer());
        BlockPos firstSolid = null;
        int y = ((HighJump)this.module).scaffoldOffset.getValue();
        while ((double)y <= ((HighJump)this.module).range.getValue()) {
            p = pos.down(y);
            IBlockState state = ListenerObby.mc.world.getBlockState(p);
            if (state.func_185904_a().blocksMovement() && !state.func_185904_a().isReplaceable()) {
                firstSolid = p;
                break;
            }
            ++y;
        }
        if (firstSolid == null) {
            return result;
        }
        for (y = firstSolid.getY(); y >= ((HighJump)this.module).scaffoldOffset.getValue(); --y) {
            p = pos.down(y);
            if (p.equals(firstSolid)) continue;
            result.getTargets().add(p);
        }
        return result;
    }

    @Override
    protected void disableModule() {
    }
}
