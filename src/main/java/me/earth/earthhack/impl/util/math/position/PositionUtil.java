package me.earth.earthhack.impl.util.math.position;

import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PositionUtil
implements Globals {
    public static BlockPos getPosition() {
        return PositionUtil.getPosition(RotationUtil.getRotationPlayer());
    }

    public static BlockPos getPosition(Entity entity) {
        return PositionUtil.getPosition(entity, 0.0);
    }

    public static BlockPos getPosition(Entity entity, double yOffset) {
        double y = entity.posY + yOffset;
        if (entity.posY - Math.floor(entity.posY) > 0.5) {
            y = Math.ceil(entity.posY);
        }
        return new BlockPos(entity.posX, y, entity.posZ);
    }

    public static Vec3d getEyePos() {
        return PositionUtil.getEyePos(PositionUtil.mc.player);
    }

    public static Vec3d getEyePos(Entity entity) {
        return new Vec3d(entity.posX, PositionUtil.getEyeHeight(entity), entity.posZ);
    }

    public static double getEyeHeight() {
        return PositionUtil.getEyeHeight(PositionUtil.mc.player);
    }

    public static double getEyeHeight(Entity entity) {
        return entity.posY + (double)entity.getEyeHeight();
    }

    public static Set<BlockPos> getBlockedPositions(Entity entity) {
        return PositionUtil.getBlockedPositions(entity.getEntityBoundingBox());
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb) {
        return PositionUtil.getBlockedPositions(bb, 0.5);
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb, double offset) {
        HashSet<BlockPos> positions = new HashSet<BlockPos>();
        double y = bb.minY;
        if (bb.minY - Math.floor(bb.minY) > offset) {
            y = Math.ceil(bb.minY);
        }
        positions.add(new BlockPos(bb.maxX, y, bb.maxZ));
        positions.add(new BlockPos(bb.minX, y, bb.minZ));
        positions.add(new BlockPos(bb.maxX, y, bb.minZ));
        positions.add(new BlockPos(bb.minX, y, bb.maxZ));
        return positions;
    }

    public static boolean isBoxColliding() {
        return PositionUtil.mc.world.getCollisionBoxes(PositionUtil.mc.player, PositionUtil.mc.player.getEntityBoundingBox().offset(0.0, 0.21, 0.0)).size() > 0;
    }

    public static boolean inLiquid() {
        return PositionUtil.inLiquid(MathHelper.floor((double)(PositionUtil.mc.player.getEntityBoundingBox().minY + 0.01)));
    }

    public static boolean inLiquid(boolean feet) {
        return PositionUtil.inLiquid(MathHelper.floor((double)(PositionUtil.mc.player.getEntityBoundingBox().minY - (feet ? 0.03 : 0.2))));
    }

    private static boolean inLiquid(int y) {
        return PositionUtil.findState(BlockLiquid.class, y) != null;
    }

    private static IBlockState findState(Class<? extends Block> block, int y) {
        int startX = MathHelper.floor((double)PositionUtil.mc.player.getEntityBoundingBox().minX);
        int startZ = MathHelper.floor((double)PositionUtil.mc.player.getEntityBoundingBox().minZ);
        int endX = MathHelper.ceil((double)PositionUtil.mc.player.getEntityBoundingBox().maxX);
        int endZ = MathHelper.ceil((double)PositionUtil.mc.player.getEntityBoundingBox().maxZ);
        for (int x = startX; x < endX; ++x) {
            for (int z = startZ; z < endZ; ++z) {
                IBlockState s = PositionUtil.mc.world.getBlockState(new BlockPos(x, y, z));
                if (!block.isInstance(s.getBlock())) continue;
                return s;
            }
        }
        return null;
    }

    public static boolean isMovementBlocked() {
        IBlockState state = PositionUtil.findState(Block.class, MathHelper.floor((double)(PositionUtil.mc.player.getEntityBoundingBox().minY - 0.01)));
        return state != null && state.func_185904_a().blocksMovement();
    }

    public static boolean isAbove(BlockPos pos) {
        return PositionUtil.mc.player.getEntityBoundingBox().minY >= (double)pos.getY();
    }

    public static BlockPos fromBB(AxisAlignedBB bb) {
        return new BlockPos((bb.minX + bb.maxX) / 2.0, (bb.minY + bb.maxY) / 2.0, (bb.minZ + bb.maxZ) / 2.0);
    }

    public static AxisAlignedBB serverBB(Entity entity) {
        return PositionUtil.newBB((double)entity.serverPosX / 4096.0, (double)entity.serverPosY / 4096.0, (double)entity.serverPosZ / 4096.0, entity.width / 2.0f, entity.height);
    }

    public static AxisAlignedBB newBB(double x, double y, double z, double w, double h) {
        return new AxisAlignedBB(x - w, y, z - w, x + w, y + h, z + w);
    }

    public static boolean intersects(AxisAlignedBB bb, BlockPos pos) {
        return bb.intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
}
