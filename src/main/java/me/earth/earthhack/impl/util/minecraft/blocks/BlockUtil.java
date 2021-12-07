/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.util.minecraft.blocks;

import java.util.List;
import java.util.function.Predicate;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class BlockUtil
implements Globals {
    public static boolean isCrystalPosInRange(BlockPos pos, double placeRange, double placeTrace, double combinedTrace) {
        double distance = BlockUtil.getDistanceSq(pos);
        if (distance > MathUtil.square(placeRange)) {
            return false;
        }
        if (distance > MathUtil.square(placeTrace) && !RayTraceUtil.raytracePlaceCheck((Entity)BlockUtil.mc.player, pos)) {
            return false;
        }
        if (distance <= MathUtil.square(combinedTrace)) {
            return true;
        }
        return RayTraceUtil.canBeSeen(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 2.7, (double)pos.getZ() + 0.5), (Entity)BlockUtil.mc.player);
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean ignoreCrystals, boolean noBoost2) {
        return BlockUtil.canPlaceCrystal(pos, ignoreCrystals, noBoost2, null);
    }

    public static double getDistanceSq(BlockPos pos) {
        return BlockUtil.getDistanceSq((Entity)RotationUtil.getRotationPlayer(), pos);
    }

    public static double getDistanceSq(Entity from, BlockPos to) {
        return from.getDistanceSqToCenter(to);
    }

    public static double getDistanceSqDigging(BlockPos to) {
        return BlockUtil.getDistanceSqDigging((Entity)RotationUtil.getRotationPlayer(), to);
    }

    public static boolean sphere(double radius, Predicate<BlockPos> predicate) {
        return BlockUtil.sphere(PositionUtil.getPosition(), radius, predicate);
    }

    public static boolean sphere(BlockPos pos, double r, Predicate<BlockPos> predicate) {
        int tested = 0;
        double rSquare = r * r;
        int x = pos.getX() - (int)r;
        while ((double)x <= (double)pos.getX() + r) {
            int z = pos.getZ() - (int)r;
            while ((double)z <= (double)pos.getZ() + r) {
                int y = pos.getY() - (int)r;
                while ((double)y < (double)pos.getY() + r) {
                    double dist = (pos.getX() - x) * (pos.getX() - x) + (pos.getZ() - z) * (pos.getZ() - z) + (pos.getY() - y) * (pos.getY() - y);
                    if (dist < rSquare && tested++ > 0 && predicate.test(new BlockPos(x, y, z))) {
                        return false;
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return true;
    }

    public static double getDistanceSqDigging(Entity from, BlockPos to) {
        double x = from.posX - ((double)to.getX() + 0.5);
        double y = from.posY - ((double)to.getY() + 0.5) + 1.5;
        double z = from.posZ - ((double)to.getZ() + 0.5);
        return x * x + y * y + z * z;
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean ignoreCrystals, boolean noBoost2, List<Entity> entities) {
        return BlockUtil.canPlaceCrystal(pos, ignoreCrystals, noBoost2, entities, noBoost2, 0L);
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean ignoreCrystals, boolean noBoost2, List<Entity> entities, boolean ignoreBoost2Entities, long deathTime) {
        IBlockState state = BlockUtil.mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) {
            return false;
        }
        return BlockUtil.checkBoost(pos, ignoreCrystals, noBoost2, entities, ignoreBoost2Entities, deathTime);
    }

    public static boolean canPlaceCrystalReplaceable(BlockPos pos, boolean ignoreCrystals, boolean noBoost2, List<Entity> entities, boolean ignoreBoost2Entities, long deathTime) {
        IBlockState state = BlockUtil.mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK && !state.getMaterial().isReplaceable()) {
            return false;
        }
        return BlockUtil.checkBoost(pos, ignoreCrystals, noBoost2, entities, ignoreBoost2Entities, deathTime);
    }

    public static boolean checkBoost(BlockPos pos, boolean ignoreCrystals, boolean noBoost2, List<Entity> entities, boolean ignoreBoost2Entities, long deathTime) {
        BlockPos boost = pos.up();
        if (BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || !BlockUtil.checkEntityList(boost, ignoreCrystals, entities, deathTime)) {
            return false;
        }
        if (!noBoost2) {
            BlockPos boost2 = boost.up();
            if (BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            return ignoreBoost2Entities || BlockUtil.checkEntityList(boost2, ignoreCrystals, entities, deathTime);
        }
        return true;
    }

    public static boolean isSemiSafe(EntityPlayer player, boolean ignoreCrystals, boolean noBoost2) {
        BlockPos origin = PositionUtil.getPosition((Entity)player);
        int i = 0;
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = origin.offset(face);
            if (BlockUtil.mc.world.getBlockState(off).getBlock() == Blocks.AIR) continue;
            ++i;
        }
        return i >= 3;
    }

    public static boolean canBeFeetPlaced(EntityPlayer player, boolean ignoreCrystals, boolean noBoost2) {
        BlockPos origin = PositionUtil.getPosition((Entity)player).down();
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = origin.offset(face);
            IBlockState state = BlockUtil.mc.world.getBlockState(off);
            if (BlockUtil.canPlaceCrystal(off, ignoreCrystals, noBoost2)) {
                return true;
            }
            BlockPos off2 = off.offset(face);
            if (!BlockUtil.canPlaceCrystal(off2, ignoreCrystals, noBoost2) || state.getBlock() != Blocks.AIR) continue;
            return true;
        }
        return false;
    }

    public static boolean canPlaceCrystalFuture(BlockPos pos, boolean ignoreCrystals, boolean noBoost2) {
        IBlockState state = BlockUtil.mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) {
            return false;
        }
        BlockPos boost = pos.up();
        if (!BlockUtil.checkEntityList(boost, ignoreCrystals, null)) {
            return false;
        }
        if (BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.BEDROCK) {
            return false;
        }
        if (!noBoost2) {
            BlockPos boost2 = boost.up();
            if (BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            return BlockUtil.checkEntityList(boost2, ignoreCrystals, null);
        }
        return true;
    }

    public static boolean isAtFeet(List<EntityPlayer> players, BlockPos pos, boolean ignoreCrystals, boolean noBoost2) {
        for (EntityPlayer player : players) {
            if (Managers.FRIENDS.contains(player) || player == BlockUtil.mc.player || !BlockUtil.isAtFeet(player, pos, ignoreCrystals, noBoost2)) continue;
            return true;
        }
        return false;
    }

    public static boolean isAtFeet(EntityPlayer player, BlockPos pos, boolean ignoreCrystals, boolean noBoost2) {
        BlockPos up = pos.up();
        if (!BlockUtil.canPlaceCrystal(pos, ignoreCrystals, noBoost2)) {
            return false;
        }
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = up.offset(face);
            IBlockState state = BlockUtil.mc.world.getBlockState(off);
            if (BlockUtil.mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(off)).contains((Object)player)) {
                return true;
            }
            BlockPos off2 = off.offset(face);
            IBlockState offState = BlockUtil.mc.world.getBlockState(off2);
            if (!BlockUtil.mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(off2)).contains((Object)player)) continue;
            return true;
        }
        return false;
    }

    public static boolean canPlaceBed(BlockPos pos, boolean newerVer) {
        if (!BlockUtil.bedBlockCheck(pos, newerVer)) {
            return false;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos horizontal = pos.offset(facing);
            if (!BlockUtil.bedBlockCheck(horizontal, newerVer) || BlockUtil.getFacing(horizontal) == null) continue;
            return true;
        }
        return false;
    }

    public static boolean checkEntityList(BlockPos pos, boolean ignoreCrystals, List<Entity> entities) {
        return BlockUtil.checkEntityList(pos, ignoreCrystals, entities, 0L);
    }

    public static boolean checkEntityList(BlockPos pos, boolean ignoreCrystals, List<Entity> entities, long deathTime) {
        if (entities == null) {
            return BlockUtil.checkEntities(pos, ignoreCrystals, deathTime);
        }
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities) {
            if (!BlockUtil.checkEntity(entity, ignoreCrystals, deathTime) || !entity.getEntityBoundingBox().intersects(bb)) continue;
            return false;
        }
        return true;
    }

    public static boolean isAir(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    public static boolean checkEntities(BlockPos pos, boolean ignoreCrystals, long deathTime) {
        for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!BlockUtil.checkEntity(entity, ignoreCrystals, deathTime)) continue;
            return false;
        }
        return true;
    }

    private static boolean checkEntity(Entity entity, boolean ignoreCrystals, long deathTime) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof EntityEnderCrystal) {
            if (ignoreCrystals) {
                return false;
            }
            return !entity.isDead || !Managers.SET_DEAD.passedDeathTime(entity, deathTime);
        }
        return !EntityUtil.isDead(entity);
    }

    public static EnumFacing getFacing(BlockPos pos) {
        return BlockUtil.getFacing(pos, (IBlockAccess)BlockUtil.mc.world);
    }

    public static EnumFacing getFacing(BlockPos pos, IBlockAccess provider) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (provider.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) continue;
            return facing;
        }
        return null;
    }

    public static boolean isReplaceable(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    public static boolean isBlocking(BlockPos pos, EntityPlayer player, BlockingType type) {
        AxisAlignedBB posBB = new AxisAlignedBB(pos);
        if (type == BlockingType.Strict || type == BlockingType.Crystals) {
            return player.getEntityBoundingBox().intersects(posBB);
        }
        if (type == BlockingType.PacketFly) {
            return player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625).intersects(posBB);
        }
        if (type == BlockingType.Full && player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625).intersects(posBB)) {
            return true;
        }
        AxisAlignedBB bb = player.getEntityBoundingBox();
        if (type == BlockingType.All) {
            bb = bb.expand(-0.0625, -0.0625, -0.0625);
        }
        if (type == BlockingType.NoPacketFly && bb.intersects(posBB)) {
            BlockPos playerPos = new BlockPos((Entity)player);
            if (playerPos.getX() != pos.getX() || playerPos.getZ() != pos.getZ()) {
                if (playerPos.getY() < pos.getY()) {
                    return BlockUtil.mc.world.getBlockState(pos.down()).getMaterial().isReplaceable();
                }
                return BlockUtil.mc.world.getBlockState(pos.up()).getMaterial().isReplaceable();
            }
            return true;
        }
        return false;
    }

    private static boolean bedBlockCheck(BlockPos pos, boolean newerVer) {
        return BlockUtil.mc.world.getBlockState(pos).getMaterial().isReplaceable() && (newerVer || !BlockUtil.mc.world.getBlockState(pos.down()).getMaterial().isReplaceable());
    }
}

