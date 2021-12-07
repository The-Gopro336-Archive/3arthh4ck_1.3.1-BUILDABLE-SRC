/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.util.vector.Matrix4f
 *  org.lwjgl.util.vector.Vector4f
 */
package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.impl.core.mixins.render.IActiveRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class VectorUtil {
    static Matrix4f modelMatrix = new Matrix4f();
    static Matrix4f projectionMatrix = new Matrix4f();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Plane toScreen(double x, double y, double z) {
        Entity view = mc.getRenderViewEntity();
        if (view == null) {
            return new Plane(0.0, 0.0, false);
        }
        Vec3d camPos = ActiveRenderInfo.getCameraPosition();
        Vec3d eyePos = ActiveRenderInfo.projectViewFromEntity((Entity)view, (double)mc.getRenderPartialTicks());
        float vecX = (float)(camPos.x + eyePos.x - (double)((float)x));
        float vecY = (float)(camPos.y + eyePos.y - (double)((float)y));
        float vecZ = (float)(camPos.z + eyePos.z - (double)((float)z));
        Vector4f pos = new Vector4f(vecX, vecY, vecZ, 1.0f);
        modelMatrix.load(IActiveRenderInfo.getModelview().asReadOnlyBuffer());
        projectionMatrix.load(IActiveRenderInfo.getProjection().asReadOnlyBuffer());
        VectorUtil.VecTransformCoordinate(pos, modelMatrix);
        VectorUtil.VecTransformCoordinate(pos, projectionMatrix);
        if (pos.w > 0.0f) {
            pos.x *= -100000.0f;
            pos.y *= -100000.0f;
        } else {
            float invert = 1.0f / pos.w;
            pos.x *= invert;
            pos.y *= invert;
        }
        ScaledResolution res = new ScaledResolution(mc);
        float halfWidth = (float)res.getScaledWidth() / 2.0f;
        float halfHeight = (float)res.getScaledHeight() / 2.0f;
        pos.x = halfWidth + (0.5f * pos.x * (float)res.getScaledWidth() + 0.5f);
        pos.y = halfHeight - (0.5f * pos.y * (float)res.getScaledHeight() + 0.5f);
        boolean bVisible = true;
        if (pos.x < 0.0f || pos.y < 0.0f || pos.x > (float)res.getScaledWidth() || pos.y > (float)res.getScaledHeight()) {
            bVisible = false;
        }
        return new Plane(pos.x, pos.y, bVisible);
    }

    private static void VecTransformCoordinate(Vector4f vec, Matrix4f matrix) {
        float x = vec.x;
        float y = vec.y;
        float z = vec.z;
        vec.x = x * matrix.m00 + y * matrix.m10 + z * matrix.m20 + matrix.m30;
        vec.y = x * matrix.m01 + y * matrix.m11 + z * matrix.m21 + matrix.m31;
        vec.z = x * matrix.m02 + y * matrix.m12 + z * matrix.m22 + matrix.m32;
        vec.w = x * matrix.m03 + y * matrix.m13 + z * matrix.m23 + matrix.m33;
    }

    public static class Plane {
        private final double x;
        private final double y;
        private final boolean visible;

        public Plane(double x, double y, boolean visible) {
            this.x = x;
            this.y = y;
            this.visible = visible;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public boolean isVisible() {
            return this.visible;
        }
    }
}

