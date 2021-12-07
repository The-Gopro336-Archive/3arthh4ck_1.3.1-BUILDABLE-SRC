package me.earth.earthhack.impl.util.math.rotation;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.function.BiPredicate;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RotationUtil
implements Globals {
    private static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);

    public static EntityPlayer getRotationPlayer() {
        EntityPlayer rotationEntity = RotationUtil.mc.player;
        if (FREECAM.isEnabled()) {
            rotationEntity = ((Freecam)FREECAM.get()).getPlayer();
        }
        return rotationEntity == null ? RotationUtil.mc.player : rotationEntity;
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing) {
        return RotationUtil.getRotations(pos, facing, RotationUtil.getRotationPlayer());
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from) {
        return RotationUtil.getRotations(pos, facing, from, RotationUtil.mc.world, RotationUtil.mc.world.getBlockState(pos));
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from, IBlockAccess world, IBlockState state) {
        AxisAlignedBB bb = state.func_185900_c(world, pos);
        double x = (double)pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = (double)pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = (double)pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;
        if (facing != null) {
            x += (double)facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += (double)facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += (double)facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }
        return RotationUtil.getRotations(x, y, z, from);
    }

    public static float[] getRotationsToTopMiddle(BlockPos pos) {
        return RotationUtil.getRotations((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
    }

    public static float[] getRotationsMaxYaw(BlockPos pos, float max, float current) {
        return new float[]{RotationUtil.updateRotation(current, RotationUtil.getRotationsToTopMiddle(pos)[0], max), RotationUtil.getRotationsToTopMiddle(pos)[1]};
    }

    public static float[] getRotations(Entity entity, double height) {
        return RotationUtil.getRotations(entity.posX, entity.posY + (double)entity.getEyeHeight() * height, entity.posZ);
    }

    public static float[] getRotations(Entity entity) {
        return RotationUtil.getRotations(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
    }

    public static float[] getRotationsMaxYaw(Entity entity, float max, float current) {
        return new float[]{MathHelper.wrapDegrees((float)RotationUtil.updateRotation(current, RotationUtil.getRotations(entity)[0], max)), RotationUtil.getRotations(entity)[1]};
    }

    public static float[] getRotations(Vec3d vec3d) {
        return RotationUtil.getRotations(vec3d.x, vec3d.y, vec3d.z);
    }

    public static float[] getRotations(double x, double y, double z) {
        return RotationUtil.getRotations(x, y, z, RotationUtil.getRotationPlayer());
    }

    public static float[] getRotations(double x, double y, double z, Entity f) {
        return RotationUtil.getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }

    public static float[] getRotations(double x, double y, double z, double fromX, double fromY, double fromZ, float fromHeight) {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + (double)fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt((double)(xDiff * xDiff + zDiff * zDiff));
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float prevYaw = Managers.ROTATION.getServerYaw();
        float diff = yaw - prevYaw;
        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - 360.0f * round;
        }
        return new float[]{prevYaw + diff, pitch};
    }

    public static boolean inFov(Entity entity) {
        return RotationUtil.inFov(entity.posX, entity.posY + (double)(entity.getEyeHeight() / 2.0f), entity.posZ);
    }

    public static boolean inFov(BlockPos pos) {
        return RotationUtil.inFov((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    public static boolean inFov(double x, double y, double z) {
        Entity renderEntity = RenderUtil.getEntity();
        if (renderEntity == null) {
            return false;
        }
        Frustum frustum = Interpolation.createFrustum(renderEntity);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(x - 1.0, y - 1.0, x - 1.0, x + 1.0, y + 1.0, z + 1.0));
    }

    public static double getAngle(Entity entity, double yOffset) {
        Vec3d vec3d = MathUtil.fromTo(Interpolation.interpolatedEyePos(), entity.posX, entity.posY + yOffset, entity.posZ);
        return MathUtil.angle(vec3d, Interpolation.interpolatedEyeVec());
    }

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin((float)MathUtil.rad(yaw)) * MathHelper.cos((float)MathUtil.rad(pitch));
        float vz = MathHelper.cos((float)MathUtil.rad(yaw)) * MathHelper.cos((float)MathUtil.rad(pitch));
        float vy = -MathHelper.sin((float)MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public static boolean isLegit(Entity entity, Entity ... additional) {
        RayTraceResult result = RayTracer.rayTraceEntities((World)RotationUtil.mc.world, (Entity)RotationUtil.getRotationPlayer(), (double)RotationUtil.getRotationPlayer().getDistance(entity) + 1.0, Managers.POSITION, Managers.ROTATION, e -> e != null && e.equals(entity), additional);
        return result != null && result.entityHit != null && (entity.equals(result.entityHit) || additional != null && additional.length != 0 && Arrays.stream(additional).anyMatch(e -> result.entityHit.equals(e)));
    }

    public static boolean isLegit(BlockPos pos) {
        return RotationUtil.isLegit(pos, null);
    }

    public static boolean isLegit(BlockPos pos, EnumFacing facing) {
        return RotationUtil.isLegit(pos, facing, RotationUtil.mc.world);
    }

    public static boolean isLegit(BlockPos pos, EnumFacing facing, IBlockAccess world) {
        RayTraceResult ray = RotationUtil.rayTraceTo(pos, world);
        return ray != null && ray.getBlockPos() != null && ray.getBlockPos().equals(pos) && (facing == null || ray.sideHit == facing);
    }

    public static RayTraceResult rayTraceTo(BlockPos pos, IBlockAccess world) {
        return RotationUtil.rayTraceTo(pos, world, (b, p) -> p.equals(pos));
    }

    public static RayTraceResult rayTraceTo(BlockPos pos, IBlockAccess world, BiPredicate<Block, BlockPos> check) {
        EntityPlayer from = RotationUtil.getRotationPlayer();
        float yaw = Managers.ROTATION.getServerYaw();
        float pitch = Managers.ROTATION.getServerPitch();
        Vec3d start = Managers.POSITION.getVec().addVector(0.0, from.getEyeHeight(), 0.0);
        Vec3d look = RotationUtil.getVec3d(yaw, pitch);
        double d = from.getDistance((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) + 1.0;
        Vec3d end = start.addVector(look.x * d, look.y * d, look.z * d);
        return RayTracer.trace(RotationUtil.mc.world, world, start, end, true, false, true, check);
    }

    public static RayTraceResult rayTraceWithYP(BlockPos pos, IBlockAccess world, float yaw, float pitch, BiPredicate<Block, BlockPos> check) {
        EntityPlayer from = RotationUtil.getRotationPlayer();
        Vec3d start = from.getPositionVector().addVector(0.0, from.getEyeHeight(), 0.0);
        Vec3d look = RotationUtil.getVec3d(yaw, pitch);
        double d = from.getDistance((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) + 1.0;
        Vec3d end = start.addVector(look.x * d, look.y * d, look.z * d);
        return RayTracer.trace(RotationUtil.mc.world, world, start, end, true, false, true, check);
    }

    public static float[] faceSmoothly(double curYaw, double curPitch, double intendedYaw, double intendedPitch, double yawSpeed, double pitchSpeed) {
        float yaw = RotationUtil.updateRotation((float)curYaw, (float)intendedYaw, (float)yawSpeed);
        float pitch = RotationUtil.updateRotation((float)curPitch, (float)intendedPitch, (float)pitchSpeed);
        return new float[]{yaw, pitch};
    }

    public static double angle(float[] rotation1, float[] rotation2) {
        Vec3d r1Vec = RotationUtil.getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = RotationUtil.getVec3d(rotation2[0], rotation2[1]);
        return MathUtil.angle(r1Vec, r2Vec);
    }

    public static float updateRotation(float current, float intended, float factor) {
        float updated = MathHelper.wrapDegrees((float)(intended - current));
        if (updated > factor) {
            updated = factor;
        }
        if (updated < -factor) {
            updated = -factor;
        }
        return current + updated;
    }

    public static int getDirection4D() {
        return MathHelper.floor((double)((double)(RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        switch (RotationUtil.getDirection4D()) {
            case 0: {
                return "South \u00a77[\u00a7f+Z\u00a77]";
            }
            case 1: {
                return "West \u00a77[\u00a7f-X\u00a77]";
            }
            case 2: {
                return (northRed ? ChatFormatting.RED : "") + "North " + "\u00a77" + "[" + "\u00a7f" + "-Z" + "\u00a77" + "]";
            }
        }
        return "East \u00a77[\u00a7f+X\u00a77]";
    }
}
