/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.util.math.raytrace;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import me.earth.earthhack.impl.managers.minecraft.movement.RotationManager;
import me.earth.earthhack.impl.util.math.path.TriPredicate;
import me.earth.earthhack.impl.util.math.raytrace.CollisionFunction;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RayTracer
implements Globals {
    private static final Predicate<Entity> PREDICATE = Predicates.and((Predicate)EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith());

    public static RayTraceResult rayTraceEntities(World world, Entity from, double range, PositionManager position, RotationManager rotation, Predicate<Entity> entityCheck, Entity ... additional) {
        return RayTracer.rayTraceEntities(world, from, range, position.getX(), position.getY(), position.getZ(), rotation.getServerYaw(), rotation.getServerPitch(), position.getBB(), entityCheck, additional);
    }

    public static RayTraceResult rayTraceEntities(World world, Entity from, double range, double posX, double posY, double posZ, float yaw, float pitch, AxisAlignedBB fromBB, Predicate<Entity> entityCheck, Entity ... additional) {
        Vec3d eyePos = new Vec3d(posX, posY + (double)from.getEyeHeight(), posZ);
        Vec3d rot = RotationUtil.getVec3d(yaw, pitch);
        Vec3d intercept = eyePos.add(rot.x * range, rot.y * range, rot.z * range);
        Entity pointedEntity = null;
        Vec3d hitVec = null;
        double distance = range;
        AxisAlignedBB within = fromBB.expand(rot.x * range, rot.y * range, rot.z * range).grow(1.0, 1.0, 1.0);
        Predicate predicate = entityCheck == null ? PREDICATE : Predicates.and(PREDICATE, entityCheck);
        List entities = mc.isCallingFromMinecraftThread() ? world.getEntitiesInAABBexcluding(from, within, predicate) : Managers.ENTITIES.getEntities().stream().filter(e -> e != null && e.getEntityBoundingBox().intersects(within) && predicate.test(e)).collect(Collectors.toList());
        for (Entity entity : additional) {
            if (entity == null || !entity.getEntityBoundingBox().intersects(within)) continue;
            entities.add(entity);
        }
        for (Entity entity : entities) {
            double hitDistance;
            AxisAlignedBB bb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
            RayTraceResult result = bb.calculateIntercept(eyePos, intercept);
            if (bb.contains(eyePos)) {
                if (!(distance >= 0.0)) continue;
                pointedEntity = entity;
                hitVec = result == null ? eyePos : result.hitVec;
                distance = 0.0;
                continue;
            }
            if (result == null || !((hitDistance = eyePos.distanceTo(result.hitVec)) < distance) && distance != 0.0) continue;
            if (entity.getLowestRidingEntity() == from.getLowestRidingEntity() && !entity.canRiderInteract()) {
                if (distance != 0.0) continue;
                pointedEntity = entity;
                hitVec = result.hitVec;
                continue;
            }
            pointedEntity = entity;
            hitVec = result.hitVec;
            distance = hitDistance;
        }
        if (pointedEntity != null && hitVec != null) {
            return new RayTraceResult(pointedEntity, hitVec);
        }
        return null;
    }

    public static RayTraceResult trace(World world, IBlockAccess access, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        return RayTracer.trace(world, access, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, null);
    }

    public static RayTraceResult trace(World world, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, BiPredicate<Block, BlockPos> blockChecker) {
        return RayTracer.trace(world, (IBlockAccess)world, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, blockChecker);
    }

    public static RayTraceResult trace(World world, IBlockAccess access, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, BiPredicate<Block, BlockPos> blockChecker) {
        return RayTracer.traceTri(world, access, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, blockChecker == null ? null : (b, p, ef) -> blockChecker.test((Block)b, (BlockPos)p));
    }

    public static RayTraceResult traceTri(World world, IBlockAccess access, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, TriPredicate<Block, BlockPos, EnumFacing> blockChecker) {
        return RayTracer.traceTri(world, access, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, blockChecker, null);
    }

    public static RayTraceResult traceTri(World world, IBlockAccess access, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, TriPredicate<Block, BlockPos, EnumFacing> blockChecker, TriPredicate<Block, BlockPos, EnumFacing> collideCheck) {
        return RayTracer.traceTri(world, access, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, blockChecker, collideCheck, CollisionFunction.DEFAULT);
    }

    public static RayTraceResult traceTri(World world, IBlockAccess access, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, TriPredicate<Block, BlockPos, EnumFacing> blockChecker, TriPredicate<Block, BlockPos, EnumFacing> collideCheck, CollisionFunction crt) {
        if (!(Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z))) {
            if (!(Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z))) {
                RayTraceResult raytraceresult;
                int feX = MathHelper.floor((double)end.x);
                int feY = MathHelper.floor((double)end.y);
                int feZ = MathHelper.floor((double)end.z);
                int fsX = MathHelper.floor((double)start.x);
                int fsY = MathHelper.floor((double)start.y);
                int fsZ = MathHelper.floor((double)start.z);
                BlockPos pos = new BlockPos(fsX, fsY, fsZ);
                IBlockState state = access.getBlockState(pos);
                Block block = state.getBlock();
                if ((!ignoreBlockWithoutBoundingBox || state.getCollisionBoundingBox(access, pos) != Block.NULL_AABB) && (block.canCollideCheck(state, stopOnLiquid) || collideCheck != null && collideCheck.test(block, pos, null)) && (blockChecker == null || blockChecker.test(block, pos, null)) && (raytraceresult = crt.collisionRayTrace(state, world, pos, start, end)) != null) {
                    return raytraceresult;
                }
                RayTraceResult result = null;
                int steps = 200;
                while (steps-- >= 0) {
                    EnumFacing enumfacing;
                    if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
                        return null;
                    }
                    if (fsX == feX && fsY == feY && fsZ == feZ) {
                        return returnLastUncollidableBlock ? result : null;
                    }
                    boolean xEq = true;
                    boolean yEq = true;
                    boolean zEq = true;
                    double x = 999.0;
                    double y = 999.0;
                    double z = 999.0;
                    if (feX > fsX) {
                        x = (double)fsX + 1.0;
                    } else if (feX < fsX) {
                        x = (double)fsX + 0.0;
                    } else {
                        xEq = false;
                    }
                    if (feY > fsY) {
                        y = (double)fsY + 1.0;
                    } else if (feY < fsY) {
                        y = (double)fsY + 0.0;
                    } else {
                        yEq = false;
                    }
                    if (feZ > fsZ) {
                        z = (double)fsZ + 1.0;
                    } else if (feZ < fsZ) {
                        z = (double)fsZ + 0.0;
                    } else {
                        zEq = false;
                    }
                    double xOff = 999.0;
                    double yOff = 999.0;
                    double zOff = 999.0;
                    double diffX = end.x - start.x;
                    double diffY = end.y - start.y;
                    double diffZ = end.z - start.z;
                    if (xEq) {
                        xOff = (x - start.x) / diffX;
                    }
                    if (yEq) {
                        yOff = (y - start.y) / diffY;
                    }
                    if (zEq) {
                        zOff = (z - start.z) / diffZ;
                    }
                    if (xOff == -0.0) {
                        xOff = -1.0E-4;
                    }
                    if (yOff == -0.0) {
                        yOff = -1.0E-4;
                    }
                    if (zOff == -0.0) {
                        zOff = -1.0E-4;
                    }
                    if (xOff < yOff && xOff < zOff) {
                        enumfacing = feX > fsX ? EnumFacing.WEST : EnumFacing.EAST;
                        start = new Vec3d(x, start.y + diffY * xOff, start.z + diffZ * xOff);
                    } else if (yOff < zOff) {
                        enumfacing = feY > fsY ? EnumFacing.DOWN : EnumFacing.UP;
                        start = new Vec3d(start.x + diffX * yOff, y, start.z + diffZ * yOff);
                    } else {
                        enumfacing = feZ > fsZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        start = new Vec3d(start.x + diffX * zOff, start.y + diffY * zOff, z);
                    }
                    fsX = MathHelper.floor((double)start.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    fsY = MathHelper.floor((double)start.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    fsZ = MathHelper.floor((double)start.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    pos = new BlockPos(fsX, fsY, fsZ);
                    IBlockState state1 = access.getBlockState(pos);
                    Block block1 = state1.getBlock();
                    if (ignoreBlockWithoutBoundingBox && state1.getMaterial() != Material.PORTAL && state1.getCollisionBoundingBox(access, pos) == Block.NULL_AABB) continue;
                    if ((block1.canCollideCheck(state1, stopOnLiquid) || collideCheck != null && collideCheck.test(block1, pos, enumfacing)) && (blockChecker == null || blockChecker.test(block1, pos, enumfacing))) {
                        RayTraceResult raytraceresult1 = crt.collisionRayTrace(state1, world, pos, start, end);
                        if (raytraceresult1 == null) continue;
                        return raytraceresult1;
                    }
                    result = new RayTraceResult(RayTraceResult.Type.MISS, start, enumfacing, pos);
                }
                return returnLastUncollidableBlock ? result : null;
            }
            return null;
        }
        return null;
    }
}

