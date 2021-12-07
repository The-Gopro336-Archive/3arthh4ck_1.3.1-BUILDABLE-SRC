package me.earth.earthhack.impl.util.math.raytrace;

import java.util.HashSet;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RayTraceFactory
implements Globals {
    private static final EnumFacing[] T = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN};
    private static final EnumFacing[] B = new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP};
    private static final EnumFacing[] S = new EnumFacing[]{EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN};

    private RayTraceFactory() {
        throw new AssertionError();
    }

    public static Ray fullTrace(Entity from, IBlockAccess world, BlockPos pos, double resolution) {
        Ray dumbRay = null;
        double closest = Double.MAX_VALUE;
        for (EnumFacing facing : RayTraceFactory.getOptimalFacings(from, pos)) {
            BlockPos offset = pos.offset(facing);
            IBlockState state = world.getBlockState(offset);
            if (state.func_185904_a().isReplaceable()) continue;
            Ray ray = RayTraceFactory.rayTrace(from, offset, facing.getOpposite(), world, state, resolution);
            if (ray.isLegit()) {
                return ray;
            }
            double dist = BlockUtil.getDistanceSq(from, offset);
            if (dumbRay != null && !(dist < closest)) continue;
            closest = dist;
            dumbRay = ray;
        }
        return dumbRay;
    }

    public static Ray rayTrace(Entity from, BlockPos on, EnumFacing facing, IBlockAccess access, IBlockState state, double res) {
        boolean zEq;
        Vec3d start = PositionUtil.getEyePos(from);
        AxisAlignedBB bb = state.func_185900_c(access, on);
        if (res >= 1.0) {
            float[] r = RayTraceFactory.rots(on, facing, from, access, state);
            Vec3d look = RotationUtil.getVec3d(r[0], r[1]);
            double d = RayTraceFactory.mc.playerController.getBlockReachDistance();
            Vec3d rotations = start.addVector(look.x * d, look.y * d, look.z * d);
            RayTraceResult result = RayTracer.trace((World)RayTraceFactory.mc.world, access, start, rotations, false, false, true);
            if (result == null || result.sideHit != facing || !on.equals(result.getBlockPos())) {
                return RayTraceFactory.dumbRay(on, facing, r);
            }
            return new Ray(result, r, on, facing, null).setLegit(true);
        }
        Vec3i dirVec = facing.getDirectionVec();
        double dirX = dirVec.getX() < 0 ? bb.minX : (double)dirVec.getX() * bb.maxX;
        double dirY = dirVec.getY() < 0 ? bb.minY : (double)dirVec.getY() * bb.maxY;
        double dirZ = dirVec.getZ() < 0 ? bb.minZ : (double)dirVec.getZ() * bb.maxZ;
        double minX = (double)on.getX() + dirX + (dirVec.getX() == 0 ? bb.minX : 0.0);
        double minY = (double)on.getY() + dirY + (dirVec.getY() == 0 ? bb.minY : 0.0);
        double minZ = (double)on.getZ() + dirZ + (dirVec.getZ() == 0 ? bb.minZ : 0.0);
        double maxX = (double)on.getX() + dirX + (dirVec.getX() == 0 ? bb.maxX : 0.0);
        double maxY = (double)on.getY() + dirY + (dirVec.getY() == 0 ? bb.maxY : 0.0);
        double maxZ = (double)on.getZ() + dirZ + (dirVec.getZ() == 0 ? bb.maxZ : 0.0);
        boolean xEq = Double.compare(minX, maxX) == 0;
        boolean yEq = Double.compare(minY, maxY) == 0;
        boolean bl = zEq = Double.compare(minZ, maxZ) == 0;
        if (xEq) {
            maxX = minX -= (double)dirVec.getX() * 5.0E-4;
        }
        if (yEq) {
            maxY = minY -= (double)dirVec.getY() * 5.0E-4;
        }
        if (zEq) {
            maxZ = minZ -= (double)dirVec.getZ() * 5.0E-4;
        }
        double endX = Math.max(minX, maxX) - (xEq ? 0.0 : 5.0E-4);
        double endY = Math.max(minY, maxY) - (yEq ? 0.0 : 5.0E-4);
        double endZ = Math.max(minZ, maxZ) - (zEq ? 0.0 : 5.0E-4);
        if (res <= 0.0) {
            double staX = Math.min(minX, maxX) + (xEq ? 0.0 : 5.0E-4);
            double staY = Math.min(minY, maxY) + (yEq ? 0.0 : 5.0E-4);
            double staZ = Math.min(minZ, maxZ) + (zEq ? 0.0 : 5.0E-4);
            HashSet<Vec3d> vectors = new HashSet<Vec3d>();
            vectors.add(new Vec3d(staX, staY, staZ));
            vectors.add(new Vec3d(staX, staY, endZ));
            vectors.add(new Vec3d(staX, endY, staZ));
            vectors.add(new Vec3d(staX, endY, endZ));
            vectors.add(new Vec3d(endX, staY, staZ));
            vectors.add(new Vec3d(endX, staY, endZ));
            vectors.add(new Vec3d(endX, endY, staZ));
            vectors.add(new Vec3d(endX, endY, endZ));
            double x = (endX - staX) / 2.0 + staX;
            double y = (endY - staY) / 2.0 + staY;
            double z = (endZ - staZ) / 2.0 + staZ;
            vectors.add(new Vec3d(x, y, z));
            for (Vec3d vec : vectors) {
                RayTraceResult ray = RayTracer.trace((World)RayTraceFactory.mc.world, access, start, vec, false, false, true);
                if (ray == null || !on.equals(ray.getBlockPos()) || facing != ray.sideHit) continue;
                return new Ray(ray, RayTraceFactory.rots(from, vec), on, facing, vec).setLegit(true);
            }
            return RayTraceFactory.dumbRay(on, facing, RayTraceFactory.rots(on, facing, from, access, state));
        }
        for (double x = Math.min(minX, maxX); x <= endX; x += res) {
            for (double y = Math.min(minY, maxY); y <= endY; y += res) {
                for (double z = Math.min(minZ, maxZ); z <= endZ; z += res) {
                    Vec3d vector = new Vec3d(x, y, z);
                    RayTraceResult ray = RayTracer.trace((World)RayTraceFactory.mc.world, access, start, vector, false, false, true);
                    if (ray == null || facing != ray.sideHit || !on.equals(ray.getBlockPos())) continue;
                    return new Ray(ray, RayTraceFactory.rots(from, vector), on, facing, vector).setLegit(true);
                }
            }
        }
        return RayTraceFactory.dumbRay(on, facing, RayTraceFactory.rots(on, facing, from, access, state));
    }

    public static Ray dumbRay(BlockPos on, EnumFacing offset, float[] rotations) {
        return RayTraceFactory.newRay(new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN), on, offset, rotations);
    }

    public static Ray newRay(RayTraceResult result, BlockPos on, EnumFacing offset, float[] rotations) {
        return new Ray(result, rotations, on, offset, null);
    }

    static float[] rots(Entity from, Vec3d vec3d) {
        return RotationUtil.getRotations(vec3d.x, vec3d.y, vec3d.z, from);
    }

    private static float[] rots(BlockPos pos, EnumFacing facing, Entity from, IBlockAccess world, IBlockState state) {
        return RotationUtil.getRotations(pos, facing, from, world, state);
    }

    private static EnumFacing[] getOptimalFacings(Entity player, BlockPos pos) {
        if ((double)pos.getY() > player.posY + 2.0) {
            return T;
        }
        if ((double)pos.getY() < player.posY) {
            return B;
        }
        return S;
    }
}
