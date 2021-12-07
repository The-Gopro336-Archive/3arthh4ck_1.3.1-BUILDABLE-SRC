package me.earth.earthhack.impl.util.minecraft.blocks;

import java.util.Arrays;
import java.util.HashSet;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleUtil
implements Globals {
    private static final Vec3i[] OFFSETS_2x2 = new Vec3i[]{new Vec3i(0, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 1)};
    private static final Block[] NO_BLAST = new Block[]{Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ANVIL, Blocks.ENDER_CHEST};

    public static boolean[] isHole(BlockPos pos, boolean above) {
        boolean[] result = new boolean[]{false, true};
        if (!BlockUtil.isAir(pos) || !BlockUtil.isAir(pos.up()) || above && !BlockUtil.isAir(pos.up(2))) {
            return result;
        }
        return HoleUtil.is1x1(pos, result);
    }

    public static boolean[] is1x1(BlockPos pos) {
        return HoleUtil.is1x1(pos, new boolean[]{false, true});
    }

    public static boolean[] is1x1(BlockPos pos, boolean[] result) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset;
            IBlockState state;
            if (facing == EnumFacing.UP || (state = HoleUtil.mc.world.getBlockState(offset = pos.offset(facing))).getBlock() == Blocks.BEDROCK) continue;
            if (Arrays.stream(NO_BLAST).noneMatch(b -> b == state.getBlock())) {
                return result;
            }
            result[1] = false;
        }
        result[0] = true;
        return result;
    }

    public static boolean is2x1(BlockPos pos) {
        return HoleUtil.is2x1(pos, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean is2x1(BlockPos pos, boolean upper) {
        if (upper) {
            if (!BlockUtil.isAir(pos)) return false;
            if (!BlockUtil.isAir(pos.up())) return false;
            if (BlockUtil.isAir(pos.down())) {
                return false;
            }
        }
        int airBlocks = 0;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset = pos.offset(facing);
            if (BlockUtil.isAir(offset)) {
                if (!BlockUtil.isAir(offset.up())) return false;
                if (BlockUtil.isAir(offset.down())) return false;
                for (EnumFacing offsetFacing : EnumFacing.HORIZONTALS) {
                    if (offsetFacing == facing.getOpposite()) continue;
                    IBlockState state = HoleUtil.mc.world.getBlockState(offset.offset(offsetFacing));
                    if (!Arrays.stream(NO_BLAST).noneMatch(b -> b == state.getBlock())) continue;
                    return false;
                }
                ++airBlocks;
            }
            if (airBlocks <= true) continue;
            return false;
        }
        if (airBlocks != true) return false;
        return true;
    }

    public static boolean is2x2Partial(BlockPos pos) {
        HashSet<BlockPos> positions = new HashSet<BlockPos>();
        for (Vec3i vec : OFFSETS_2x2) {
            positions.add(pos.add(vec));
        }
        boolean airBlock = false;
        for (BlockPos holePos : positions) {
            if (BlockUtil.isAir(holePos) && BlockUtil.isAir(holePos.up()) && !BlockUtil.isAir(holePos.down())) {
                if (BlockUtil.isAir(holePos.up(2))) {
                    airBlock = true;
                }
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    BlockPos offset = holePos.offset(facing);
                    if (positions.contains(offset)) continue;
                    IBlockState state = HoleUtil.mc.world.getBlockState(offset);
                    if (!Arrays.stream(NO_BLAST).noneMatch(b -> b == state.getBlock())) continue;
                    return false;
                }
                continue;
            }
            return false;
        }
        return airBlock;
    }

    public static boolean is2x2(BlockPos pos) {
        return HoleUtil.is2x2(pos, true);
    }

    public static boolean is2x2(BlockPos pos, boolean upper) {
        if (upper && !BlockUtil.isAir(pos)) {
            return false;
        }
        if (HoleUtil.is2x2Partial(pos)) {
            return true;
        }
        BlockPos l = pos.add(-1, 0, 0);
        boolean airL = BlockUtil.isAir(l);
        if (airL && HoleUtil.is2x2Partial(l)) {
            return true;
        }
        BlockPos r = pos.add(0, 0, -1);
        boolean airR = BlockUtil.isAir(r);
        if (airR && HoleUtil.is2x2Partial(r)) {
            return true;
        }
        return (airL || airR) && HoleUtil.is2x2Partial(pos.add(-1, 0, -1));
    }
}
