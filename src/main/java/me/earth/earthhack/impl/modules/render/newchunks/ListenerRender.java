package me.earth.earthhack.impl.modules.render.newchunks;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.newchunks.NewChunks;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<NewChunks, Render3DEvent> {
    public ListenerRender(NewChunks module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        boolean lightning = GL11.glIsEnabled(2896);
        boolean blend = GL11.glIsEnabled(3042);
        boolean texture = GL11.glIsEnabled(3553);
        boolean depth = GL11.glIsEnabled(2929);
        boolean lines = GL11.glIsEnabled(2848);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        if (lightning) {
            GL11.glDisable(2896);
        }
        GL11.glBlendFunc(770, 771);
        if (!blend) {
            GL11.glEnable(3042);
        }
        GL11.glLineWidth(0.5f);
        if (texture) {
            GL11.glDisable(3553);
        }
        if (depth) {
            GL11.glDisable(2929);
        }
        if (!lines) {
            GL11.glEnable(2848);
        }
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        Frustum frustum = Interpolation.createFrustum(RenderUtil.getEntity());
        for (ChunkData data : ((NewChunks)this.module).data) {
            double dZ;
            double dX = data.getX() * 16;
            AxisAlignedBB bb = new AxisAlignedBB(dX, 0.0, dZ = (double)(data.getZ() * 16), dX + 16.0, 0.0, dZ + 16.0);
            if (!frustum.isBoundingBoxInFrustum(bb)) continue;
            RenderUtil.doPosition(Interpolation.offsetRenderPos(bb));
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (!lines) {
            GL11.glDisable(2848);
        }
        if (depth) {
            GL11.glEnable(2929);
        }
        if (texture) {
            GL11.glEnable(3553);
        }
        if (!blend) {
            GL11.glDisable(3042);
        }
        if (lightning) {
            GL11.glEnable(2896);
        }
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}
