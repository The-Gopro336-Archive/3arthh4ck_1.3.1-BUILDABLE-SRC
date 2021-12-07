/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.render.search;

import java.awt.Color;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.search.Search;
import me.earth.earthhack.impl.modules.render.search.SearchResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Search, Render3DEvent> {
    public ListenerRender(Search module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        int count = 0;
        ((Search)this.module).found = 0;
        boolean inRange = ((Search)this.module).countInRange.getValue();
        boolean colored = ((Search)this.module).coloredTracers.getValue();
        double distance = MathUtil.square(((Search)this.module).range.getValue());
        Entity renderEntity = RenderUtil.getEntity();
        Frustum frustum = Interpolation.createFrustum(renderEntity);
        for (SearchResult result : ((Search)this.module).toRender.values()) {
            AxisAlignedBB bb;
            BlockPos pos;
            if (!inRange) {
                ++((Search)this.module).found;
            }
            if (!(renderEntity.getDistanceSq(pos = result.getPos()) <= distance)) continue;
            if (inRange) {
                ++((Search)this.module).found;
            }
            if (++count > ((Search)this.module).maxBlocks.getValue()) continue;
            float red = result.getRed();
            float green = result.getGreen();
            float blue = result.getBlue();
            float alpha = result.getAlpha();
            Color color = result.getColor();
            if ((((Search)this.module).lines.getValue().booleanValue() || ((Search)this.module).fill.getValue().booleanValue()) && frustum.isBoundingBoxInFrustum(bb = result.getBb())) {
                AxisAlignedBB box = Interpolation.offsetRenderPos(bb);
                if (((Search)this.module).lines.getValue().booleanValue()) {
                    RenderUtil.startRender();
                    RenderUtil.drawOutline(box, 1.5f, color);
                    RenderUtil.endRender();
                }
                if (((Search)this.module).fill.getValue().booleanValue()) {
                    RenderUtil.startRender();
                    RenderUtil.drawBox(box, color);
                    RenderUtil.endRender();
                }
            }
            if (!((Search)this.module).tracers.getValue().booleanValue()) continue;
            double x = (double)pos.getX() - Interpolation.getRenderPosX();
            double y = (double)pos.getY() - Interpolation.getRenderPosY();
            double z = (double)pos.getZ() - Interpolation.getRenderPosZ();
            if (colored) {
                GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
            } else {
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            }
            RenderUtil.startRender();
            GL11.glLoadIdentity();
            GL11.glLineWidth((float)1.5f);
            boolean viewBobbing = ListenerRender.mc.gameSettings.viewBobbing;
            ListenerRender.mc.gameSettings.viewBobbing = false;
            ((IEntityRenderer)ListenerRender.mc.entityRenderer).invokeOrientCamera(event.getPartialTicks());
            ListenerRender.mc.gameSettings.viewBobbing = viewBobbing;
            Vec3d vec3d = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(renderEntity.rotationPitch))).rotateYaw(-((float)Math.toRadians(renderEntity.rotationYaw)));
            GL11.glBegin((int)1);
            GL11.glVertex3d((double)vec3d.x, (double)((double)renderEntity.getEyeHeight() + vec3d.y), (double)vec3d.z);
            GL11.glVertex3d((double)(x + 0.5), (double)(y + 0.5), (double)(z + 0.5));
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glEnd();
            RenderUtil.endRender();
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

