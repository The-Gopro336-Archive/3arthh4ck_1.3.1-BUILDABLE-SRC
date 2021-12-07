/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.util.math.raytrace;

import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Ray {
    private final RayTraceResult result;
    private final EnumFacing facing;
    private final BlockPos pos;
    private final Vec3d vector;
    private float[] rotations;
    private boolean legit;

    public Ray(RayTraceResult result, float[] rotations, BlockPos pos, EnumFacing facing, Vec3d vector) {
        this.result = result;
        this.rotations = rotations;
        this.pos = pos;
        this.facing = facing;
        this.vector = vector;
    }

    public RayTraceResult getResult() {
        return this.result;
    }

    public void updateRotations(Entity entity) {
        if (this.vector != null) {
            this.rotations = RayTraceFactory.rots(entity, this.vector);
        }
    }

    public float[] getRotations() {
        return this.rotations;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean isLegit() {
        return this.legit;
    }

    public Vec3d getVector() {
        return this.vector;
    }

    public Ray setLegit(boolean legit) {
        this.legit = legit;
        return this;
    }
}

