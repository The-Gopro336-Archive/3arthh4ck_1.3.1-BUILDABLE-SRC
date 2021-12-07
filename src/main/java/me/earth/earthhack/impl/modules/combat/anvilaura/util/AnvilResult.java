package me.earth.earthhack.impl.modules.combat.anvilaura.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class AnvilResult
implements Globals,
Comparable<AnvilResult> {
    private static final AxisAlignedBB ANVIL_BB = new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);
    private final Set<BlockPos> positions;
    private final Set<BlockPos> mine;
    private final Set<BlockPos> trap;
    private final EntityPlayer player;
    private final BlockPos playerPos;
    private final BlockPos pressurePos;
    private final boolean validPressure;
    private final boolean fallingEntities;
    private final boolean specialPressure;

    public AnvilResult(Set<BlockPos> positions, Set<BlockPos> mine, Set<BlockPos> trap, EntityPlayer player, BlockPos playerPos, BlockPos pressurePos, boolean validPressure, boolean fallingEntities, boolean specialPressure) {
        this.positions = positions;
        this.mine = mine;
        this.trap = trap;
        this.player = player;
        this.playerPos = playerPos;
        this.pressurePos = pressurePos;
        this.validPressure = validPressure;
        this.fallingEntities = fallingEntities;
        this.specialPressure = specialPressure;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public BlockPos getPressurePos() {
        return this.pressurePos;
    }

    public BlockPos getPlayerPos() {
        return this.playerPos;
    }

    public Set<BlockPos> getPositions() {
        return this.positions;
    }

    public Set<BlockPos> getMine() {
        return this.mine;
    }

    public Set<BlockPos> getTrap() {
        return this.trap;
    }

    public boolean hasValidPressure() {
        return this.validPressure;
    }

    public boolean hasFallingEntities() {
        return this.fallingEntities;
    }

    public boolean hasSpecialPressure() {
        return this.specialPressure;
    }

    public int hashCode() {
        return this.player.getEntityId() * 31 + this.playerPos.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AnvilResult) {
            return ((AnvilResult)obj).player.equals(this.player) && ((AnvilResult)obj).playerPos.equals(this.playerPos);
        }
        return false;
    }

    @Override
    public int compareTo(AnvilResult o) {
        if (this.equals(o)) {
            return 0;
        }
        int r = Double.compare(BlockUtil.getDistanceSq(o.playerPos), BlockUtil.getDistanceSq(this.playerPos));
        return r == 0 ? 1 : r;
    }

    public static Set<AnvilResult> create(List<EntityPlayer> players, List<Entity> entities, double minY, double range) {
        TreeSet<AnvilResult> results = new TreeSet<AnvilResult>();
        EntityPlayer rotation = RotationUtil.getRotationPlayer();
        for (EntityPlayer player : players) {
            double distance;
            if (player.posY < 0.0 || EntityUtil.isDead(player) || player.equals(RotationUtil.getRotationPlayer()) || player.equals(AnvilResult.mc.player) || Managers.FRIENDS.contains(player) || (distance = MathUtil.square(player.posX - rotation.posX) + MathUtil.square(player.posZ - rotation.posZ)) > MathUtil.square(range)) continue;
            for (BlockPos pos : PositionUtil.getBlockedPositions(player.getEntityBoundingBox(), 1.0)) {
                if (!player.getEntityBoundingBox().intersects(ANVIL_BB.offset(pos))) continue;
                AnvilResult.checkPos(player, pos, results, entities, minY, range);
            }
        }
        return results;
    }

    private static void checkPos(EntityPlayer player, BlockPos playerPos, Set<AnvilResult> results, List<Entity> entities, double minY, double range) {
        BlockPos pos;
        IBlockState state;
        BlockPos pressureDown;
        int x = playerPos.getX();
        int z = playerPos.getZ();
        BlockPos upUp = playerPos.up(2);
        LinkedHashSet<BlockPos> trap = new LinkedHashSet<BlockPos>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos trapPos = upUp.offset(facing);
            if (!ObbyModule.HELPER.getBlockState(trapPos).func_185904_a().isReplaceable()) continue;
            trap.add(trapPos);
        }
        boolean validPressure = true;
        BlockPos pressure = playerPos;
        boolean specialPressure = false;
        LinkedHashSet<BlockPos> mine = new LinkedHashSet<BlockPos>();
        IBlockState playerState = ObbyModule.HELPER.getBlockState(pressure);
        if (!playerState.func_185904_a().isReplaceable() && !SpecialBlocks.PRESSURE_PLATES.contains(playerState.getBlock())) {
            if (playerState.getBlock() == Blocks.ANVIL) {
                validPressure = false;
                mine.add(pressure);
            } else if (!AnvilResult.mc.world.mayPlace(Blocks.ANVIL, pressure, true, EnumFacing.UP, null) && playerState.func_185900_c((IBlockAccess)ObbyModule.HELPER, (BlockPos)pressure).maxY < 1.0) {
                specialPressure = true;
            }
            pressure = playerPos.up();
            playerState = ObbyModule.HELPER.getBlockState(pressure);
            if (!playerState.func_185904_a().isReplaceable()) {
                if (playerState.getBlock() == Blocks.ANVIL) {
                    mine.add(pressure);
                } else {
                    return;
                }
            }
        }
        if (validPressure && !specialPressure && !AnvilResult.isTopSolid(pressureDown = pressure.down(), (state = ObbyModule.HELPER.getBlockState(pressureDown)).getBlock(), state, EnumFacing.UP, ObbyModule.HELPER) && !(state.getBlock() instanceof BlockFence)) {
            validPressure = false;
        }
        BlockPos lowest = null;
        boolean fallingEntities = false;
        double yPos = RotationUtil.getRotationPlayer().posY;
        LinkedHashSet<BlockPos> positions = new LinkedHashSet<BlockPos>();
        for (double y = yPos - range; y < yPos + range; y += 1.0) {
            pos = new BlockPos((double)x, y, (double)z);
            boolean bl = fallingEntities = fallingEntities || AnvilResult.checkForFalling(pos, entities);
            if (y < player.posY + minY) continue;
            if (!BlockFalling.canFallThrough((IBlockState)ObbyModule.HELPER.getBlockState(pos))) break;
            if (lowest == null) {
                lowest = pos;
            }
            positions.add(pos);
        }
        if (lowest == null) {
            return;
        }
        boolean bad = false;
        for (int y = pressure.getY(); y < lowest.getY(); ++y) {
            IBlockState state2;
            pos = new BlockPos(x, y, z);
            boolean bl = fallingEntities = fallingEntities || AnvilResult.checkForFalling(pos, entities);
            if (pos.getY() == pressure.getY() || BlockFalling.canFallThrough((IBlockState)(state2 = ObbyModule.HELPER.getBlockState(pos)))) continue;
            if (state2.getBlock() == Blocks.ANVIL) {
                mine.add(pos);
                continue;
            }
            bad = true;
            break;
        }
        if (bad) {
            return;
        }
        results.add(new AnvilResult(positions, mine, trap, player, playerPos, pressure, validPressure, fallingEntities, specialPressure));
    }

    private static boolean checkForFalling(BlockPos pos, List<Entity> entities) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities) {
            if (!(entity instanceof EntityFallingBlock) || entity.isDead || !entity.getEntityBoundingBox().intersects(bb)) continue;
            return true;
        }
        return false;
    }

    private static boolean isTopSolid(BlockPos pos, Block block, IBlockState base_state, EnumFacing side, IBlockAccess world) {
        if (base_state.func_185896_q() && side == EnumFacing.UP) {
            return true;
        }
        if (block instanceof BlockSlab) {
            IBlockState state = block.getActualState(base_state, world, pos);
            return base_state.func_185913_b() || state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP && side == EnumFacing.UP || state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM && side == EnumFacing.DOWN;
        }
        if (block instanceof BlockFarmland) {
            return side != EnumFacing.DOWN && side != EnumFacing.UP;
        }
        if (block instanceof BlockStairs) {
            IBlockState state = block.getActualState(base_state, world, pos);
            boolean flipped = state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
            BlockStairs.EnumShape shape = (BlockStairs.EnumShape)((Object)state.getValue(BlockStairs.SHAPE));
            EnumFacing facing = (EnumFacing)((Object)state.getValue(BlockStairs.FACING));
            if (side == EnumFacing.UP) {
                return flipped;
            }
            if (side == EnumFacing.DOWN) {
                return !flipped;
            }
            if (facing == side) {
                return true;
            }
            if (flipped) {
                if (shape == BlockStairs.EnumShape.INNER_LEFT) {
                    return side == facing.rotateYCCW();
                }
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) {
                    return side == facing.rotateY();
                }
            } else {
                if (shape == BlockStairs.EnumShape.INNER_LEFT) {
                    return side == facing.rotateY();
                }
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) {
                    return side == facing.rotateYCCW();
                }
            }
            return false;
        }
        if (block instanceof BlockSnow) {
            IBlockState state = block.getActualState(base_state, world, pos);
            return (Integer)state.getValue(BlockSnow.LAYERS) >= 8;
        }
        if (block instanceof BlockHopper && side == EnumFacing.UP) {
            return true;
        }
        if (block instanceof BlockCompressedPowered) {
            return true;
        }
        return base_state.func_185896_q();
    }
}
