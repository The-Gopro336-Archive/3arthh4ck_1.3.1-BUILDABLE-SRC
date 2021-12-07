/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.movement.holetp.ListenerMotion;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class HoleTP
extends Module {
    public static final double[] OFFSETS = new double[]{0.42, 0.75};
    public final Setting<Boolean> wide = this.register(new BooleanSetting("2x1s", true));
    public final Setting<Boolean> big = this.register(new BooleanSetting("2x2s", false));
    protected boolean jumped;
    protected int packets;

    public HoleTP() {
        super("HoleTP", Category.Movement);
        this.listeners.add(new ListenerMotion(this));
    }

    public boolean isInHole() {
        BlockPos blockPos = new BlockPos(HoleTP.mc.player.posX, HoleTP.mc.player.posY, HoleTP.mc.player.posZ);
        IBlockState blockState = HoleTP.mc.world.getBlockState(blockPos);
        return this.isBlockValid(blockState, blockPos);
    }

    protected boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
        if (blockState.getBlock() != Blocks.AIR) {
            return false;
        }
        if (HoleTP.mc.player.getDistanceSq(blockPos) < 1.0) {
            return false;
        }
        if (HoleTP.mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }
        if (HoleTP.mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) {
            return false;
        }
        return HoleUtil.isHole(blockPos, true)[0] || HoleUtil.is2x1(blockPos) && this.wide.getValue() != false || HoleUtil.is2x2Partial(blockPos) && this.big.getValue() != false;
    }

    protected double getNearestBlockBelow() {
        for (double y = HoleTP.mc.player.posY; y > 0.0; y -= 0.001) {
            if (HoleTP.mc.world.getBlockState(new BlockPos(HoleTP.mc.player.posX, y, HoleTP.mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox((IBlockAccess)HoleTP.mc.world, new BlockPos(0, 0, 0)) == null) continue;
            if (HoleTP.mc.world.getBlockState(new BlockPos(HoleTP.mc.player.posX, y, HoleTP.mc.player.posZ)).getBlock() instanceof BlockSlab) {
                return -1.0;
            }
            return y;
        }
        return -1.0;
    }
}

