/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Interpolation
implements Globals {
    private static final ModuleCache<NoInterp> NOINTERP = Caches.getModule(NoInterp.class);

    public static Vec3d interpolatedEyePos() {
        return Interpolation.mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec() {
        return Interpolation.mc.player.getLook(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec(EntityPlayer player) {
        return player.getLook(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolateEntity(Entity entity) {
        double z;
        double y;
        double x;
        if (NOINTERP.isEnabled() && ((NoInterp)NOINTERP.get()).isSilent() && entity instanceof IEntityNoInterp && ((IEntityNoInterp)entity).isNoInterping()) {
            x = Interpolation.interpolateLastTickPos(((IEntityNoInterp)entity).getNoInterpX(), entity.lastTickPosX) - Interpolation.getRenderPosX();
            y = Interpolation.interpolateLastTickPos(((IEntityNoInterp)entity).getNoInterpY(), entity.lastTickPosY) - Interpolation.getRenderPosY();
            z = Interpolation.interpolateLastTickPos(((IEntityNoInterp)entity).getNoInterpZ(), entity.lastTickPosZ) - Interpolation.getRenderPosZ();
        } else {
            x = Interpolation.interpolateLastTickPos(entity.posX, entity.lastTickPosX) - Interpolation.getRenderPosX();
            y = Interpolation.interpolateLastTickPos(entity.posY, entity.lastTickPosY) - Interpolation.getRenderPosY();
            z = Interpolation.interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - Interpolation.getRenderPosZ();
        }
        return new Vec3d(x, y, z);
    }

    public static Vec3d interpolateVectors(Vec3d current, Vec3d last) {
        double x = Interpolation.interpolateLastTickPos(current.x, last.x);
        double y = Interpolation.interpolateLastTickPos(current.y, last.y);
        double z = Interpolation.interpolateLastTickPos(current.z, last.z);
        return new Vec3d(x, y, z);
    }

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * (double)((IMinecraft)Interpolation.mc).getTimer().renderPartialTicks;
    }

    public static AxisAlignedBB interpolatePos(BlockPos pos, float height) {
        return new AxisAlignedBB((double)pos.getX() - Interpolation.mc.getRenderManager().viewerPosX, (double)pos.getY() - Interpolation.mc.getRenderManager().viewerPosY, (double)pos.getZ() - Interpolation.mc.getRenderManager().viewerPosZ, (double)pos.getX() - Interpolation.mc.getRenderManager().viewerPosX + 1.0, (double)pos.getY() - Interpolation.mc.getRenderManager().viewerPosY + (double)height, (double)pos.getZ() - Interpolation.mc.getRenderManager().viewerPosZ + 1.0);
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - Interpolation.mc.getRenderManager().viewerPosX, bb.minY - Interpolation.mc.getRenderManager().viewerPosY, bb.minZ - Interpolation.mc.getRenderManager().viewerPosZ, bb.maxX - Interpolation.mc.getRenderManager().viewerPosX, bb.maxY - Interpolation.mc.getRenderManager().viewerPosY, bb.maxZ - Interpolation.mc.getRenderManager().viewerPosZ);
    }

    public static AxisAlignedBB offsetRenderPos(AxisAlignedBB bb) {
        return bb.offset(-Interpolation.getRenderPosX(), -Interpolation.getRenderPosY(), -Interpolation.getRenderPosZ());
    }

    public static double getRenderPosX() {
        return ((IRenderManager)mc.getRenderManager()).getRenderPosX();
    }

    public static double getRenderPosY() {
        return ((IRenderManager)mc.getRenderManager()).getRenderPosY();
    }

    public static double getRenderPosZ() {
        return ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
    }

    public static Frustum createFrustum(Entity entity) {
        Frustum frustum = new Frustum();
        double x = Interpolation.interpolateLastTickPos(entity.posX, entity.lastTickPosX);
        double y = Interpolation.interpolateLastTickPos(entity.posY, entity.lastTickPosY);
        double z = Interpolation.interpolateLastTickPos(entity.posZ, entity.lastTickPosZ);
        frustum.setPosition(x, y, z);
        return frustum;
    }
}

