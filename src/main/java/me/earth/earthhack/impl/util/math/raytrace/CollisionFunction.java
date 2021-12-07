package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@FunctionalInterface
public interface CollisionFunction {
    public static final CollisionFunction DEFAULT = IBlockProperties::collisionRayTrace;

    public RayTraceResult collisionRayTrace(IBlockState var1, World var2, BlockPos var3, Vec3d var4, Vec3d var5);
}
