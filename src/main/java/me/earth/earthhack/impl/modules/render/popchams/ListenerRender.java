/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.modules.render.popchams;

import java.awt.Color;
import java.util.Map;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.popchams.PopChams;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

final class ListenerRender
extends ModuleListener<PopChams, Render3DEvent> {
    private static final double HEAD_X = -0.2;
    private static final double HEAD_Y = 1.5;
    private static final double HEAD_Z = -0.25;
    private static final double HEAD_X1 = 0.2;
    private static final double HEAD_Y1 = 1.95;
    private static final double HEAD_Z1 = 0.25;
    private static final double CHEST_X = -0.18;
    private static final double CHEST_Y = 0.8;
    private static final double CHEST_Z = -0.275;
    private static final double CHEST_X1 = 0.18;
    private static final double CHEST_Y1 = 1.5;
    private static final double CHEST_Z1 = 0.275;
    private static final double ARM1_X = -0.1;
    private static final double ARM1_Y = 0.75;
    private static final double ARM1_Z = 0.275;
    private static final double ARM1_X1 = 0.1;
    private static final double ARM1_Y1 = 1.5;
    private static final double ARM1_Z1 = 0.5;
    private static final double ARM2_X = -0.1;
    private static final double ARM2_Y = 0.75;
    private static final double ARM2_Z = -0.275;
    private static final double ARM2_X1 = 0.1;
    private static final double ARM2_Y1 = 1.5;
    private static final double ARM2_Z1 = -0.5;
    private static final double LEG1_X = -0.15;
    private static final double LEG1_Y = 0.0;
    private static final double LEG1_Z = 0.0;
    private static final double LEG1_X1 = 0.15;
    private static final double LEG1_Y1 = 0.8;
    private static final double LEG1_Z1 = 0.25;
    private static final double LEG2_X = -0.15;
    private static final double LEG2_Y = 0.0;
    private static final double LEG2_Z = 0.0;
    private static final double LEG2_X1 = 0.15;
    private static final double LEG2_Y1 = 0.8;
    private static final double LEG2_Z1 = -0.25;

    public ListenerRender(PopChams module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        for (Map.Entry<String, PopChams.PopData> set : ((PopChams)this.module).getPopDataHashMap().entrySet()) {
            PopChams.PopData data = set.getValue();
            double x = data.getX() - ListenerRender.mc.getRenderManager().viewerPosX;
            double y = data.getY() - ListenerRender.mc.getRenderManager().viewerPosY;
            double z = data.getZ() - ListenerRender.mc.getRenderManager().viewerPosZ;
            float yaw = data.getYaw();
            float pitch = data.getPitch();
            AxisAlignedBB head = new AxisAlignedBB(x + -0.2, y + 1.5, z + -0.25, x + 0.2, y + 1.95, z + 0.25);
            AxisAlignedBB chest = new AxisAlignedBB(x + -0.18, y + 0.8, z + -0.275, x + 0.18, y + 1.5, z + 0.275);
            AxisAlignedBB arm1 = new AxisAlignedBB(x + -0.1, y + 0.75, z + 0.275, x + 0.1, y + 1.5, z + 0.5);
            AxisAlignedBB arm2 = new AxisAlignedBB(x + -0.1, y + 0.75, z + -0.275, x + 0.1, y + 1.5, z + -0.5);
            AxisAlignedBB leg1 = new AxisAlignedBB(x + -0.15, y + 0.0, z + 0.0, x + 0.15, y + 0.8, z + 0.25);
            AxisAlignedBB leg2 = new AxisAlignedBB(x + -0.15, y + 0.0, z + 0.0, x + 0.15, y + 0.8, z + -0.25);
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)x, (double)y, (double)z);
            GlStateManager.rotate((float)(180.0f + -(yaw + 90.0f)), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.translate((double)(-x), (double)(-y), (double)(-z));
            Color boxColor = ((PopChams)this.module).getColor(data.getPlayer());
            Color outlineColor = ((PopChams)this.module).getOutlineColor(data.getPlayer());
            float maxBoxAlpha = boxColor.getAlpha();
            float maxOutlineAlpha = outlineColor.getAlpha();
            float alphaBoxAmount = maxBoxAlpha / (float)((PopChams)this.module).fadeTime.getValue().intValue();
            float alphaOutlineAmount = maxOutlineAlpha / (float)((PopChams)this.module).fadeTime.getValue().intValue();
            int fadeBoxAlpha = MathHelper.clamp((int)((int)(alphaBoxAmount * (float)(data.getTime() + (long)((PopChams)this.module).fadeTime.getValue().intValue() - System.currentTimeMillis()))), (int)0, (int)((int)maxBoxAlpha));
            int fadeOutlineAlpha = MathHelper.clamp((int)((int)(alphaOutlineAmount * (float)(data.getTime() + (long)((PopChams)this.module).fadeTime.getValue().intValue() - System.currentTimeMillis()))), (int)0, (int)((int)maxOutlineAlpha));
            Color box = new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha);
            Color out = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha);
            this.renderAxis(chest, box, out);
            this.renderAxis(arm1, box, out);
            this.renderAxis(arm2, box, out);
            this.renderAxis(leg1, box, out);
            this.renderAxis(leg2, box, out);
            GlStateManager.translate((double)x, (double)(y + 1.5), (double)z);
            GlStateManager.rotate((float)pitch, (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.translate((double)(-x), (double)(-y - 1.5), (double)(-z));
            this.renderAxis(head, box, out);
            GlStateManager.popMatrix();
        }
        ((PopChams)this.module).getPopDataHashMap().entrySet().removeIf(e -> ((PopChams.PopData)e.getValue()).getTime() + (long)((PopChams)this.module).fadeTime.getValue().intValue() < System.currentTimeMillis());
    }

    private void renderAxis(AxisAlignedBB bb, Color color, Color outline) {
        RenderUtil.renderBox(bb, color, outline, ((Float)((PopChams)this.module).lineWidth.getValue()).floatValue());
    }
}

