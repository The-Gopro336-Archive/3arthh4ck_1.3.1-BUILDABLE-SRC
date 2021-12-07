package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.modules.movement.reversestep.ListenerMotion;
import net.minecraft.block.BlockSlab;
import net.minecraft.util.math.BlockPos;

public class ReverseStep
extends Module {
    protected boolean jumped;
    protected boolean waitForOnGround;
    protected int packets;

    public ReverseStep() {
        super("ReverseStep", Category.Movement);
        this.listeners.add(new ListenerMotion(this));
    }

    protected double getNearestBlockBelow() {
        for (double y = ReverseStep.mc.player.posY; y > 0.0; y -= 0.001) {
            if (ReverseStep.mc.world.getBlockState(new BlockPos(ReverseStep.mc.player.posX, y, ReverseStep.mc.player.posZ)).getBlock().getDefaultState().func_185890_d(ReverseStep.mc.world, new BlockPos(0, 0, 0)) == null) continue;
            if (ReverseStep.mc.world.getBlockState(new BlockPos(ReverseStep.mc.player.posX, y, ReverseStep.mc.player.posZ)).getBlock() instanceof BlockSlab) {
                return -1.0;
            }
            return y;
        }
        return -1.0;
    }
}
