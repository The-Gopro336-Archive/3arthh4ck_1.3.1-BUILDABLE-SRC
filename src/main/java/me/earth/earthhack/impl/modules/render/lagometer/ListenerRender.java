package me.earth.earthhack.impl.modules.render.lagometer;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerRender
extends ModuleListener<LagOMeter, Render3DEvent> {
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

    public ListenerRender(LagOMeter module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!((LagOMeter)this.module).esp.getValue().booleanValue()) {
            return;
        }
        double factor = 1.0;
        if (((LagOMeter)this.module).teleported.get()) {
            double time = ((LagOMeter)this.module).time.getValue().intValue();
            long t = System.currentTimeMillis() - Managers.NCP.getTimeStamp();
            if ((double)t > time) {
                return;
            }
            factor = MathUtil.clamp(1.0 - (double)t / time, 0.0, 1.0);
        }
        GlStateManager.pushMatrix();
        double x = ((LagOMeter)this.module).x - ListenerRender.mc.getRenderManager().viewerPosX;
        double y = ((LagOMeter)this.module).y - ListenerRender.mc.getRenderManager().viewerPosY;
        double z = ((LagOMeter)this.module).z - ListenerRender.mc.getRenderManager().viewerPosZ;
        float yaw = ((LagOMeter)this.module).yaw;
        float pitch = ((LagOMeter)this.module).pitch;
        AxisAlignedBB head = new AxisAlignedBB(x + -0.2, y + 1.5, z + -0.25, x + 0.2, y + 1.95, z + 0.25);
        AxisAlignedBB chest = new AxisAlignedBB(x + -0.18, y + 0.8, z + -0.275, x + 0.18, y + 1.5, z + 0.275);
        AxisAlignedBB arm1 = new AxisAlignedBB(x + -0.1, y + 0.75, z + 0.275, x + 0.1, y + 1.5, z + 0.5);
        AxisAlignedBB arm2 = new AxisAlignedBB(x + -0.1, y + 0.75, z + -0.275, x + 0.1, y + 1.5, z + -0.5);
        AxisAlignedBB leg1 = new AxisAlignedBB(x + -0.15, y + 0.0, z + 0.0, x + 0.15, y + 0.8, z + 0.25);
        AxisAlignedBB leg2 = new AxisAlignedBB(x + -0.15, y + 0.0, z + 0.0, x + 0.15, y + 0.8, z + -0.25);
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.rotate((float)(180.0f + -(yaw + 90.0f)), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((double)(-x), (double)(-y), (double)(-z));
        Color box = this.getColor((Color)((LagOMeter)this.module).color.getValue(), factor);
        Color out = this.getColor((Color)((LagOMeter)this.module).outline.getValue(), factor);
        this.renderAxis(chest, box, out);
        this.renderAxis(arm1, box, out);
        this.renderAxis(arm2, box, out);
        this.renderAxis(leg1, box, out);
        this.renderAxis(leg2, box, out);
        GlStateManager.translate((double)x, (double)(y + 1.5), (double)z);
        GlStateManager.rotate((float)pitch, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((double)(-x), (double)(-y - 1.5), (double)(-z));
        this.renderAxis(head, box, out);
        GlStateManager.translate((double)x, (double)(y + 1.5), (double)z);
        GlStateManager.rotate((float)(-pitch), (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((double)(-x), (double)(-y - 1.5), (double)(-z));
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.rotate((float)(180.0f + (yaw + 90.0f)), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((double)(-x), (double)(-y), (double)(-z));
        if (((LagOMeter)this.module).nametag.getValue().booleanValue()) {
            int color = ((LagOMeter)this.module).textColor.getRGB();
            double scale = ((LagOMeter)this.module).scale.getValue().floatValue();
            RenderUtil.drawNametag("Lag", x, y + 0.7, z, scale, color, false);
        }
        GlStateManager.popMatrix();
    }

    private void renderAxis(AxisAlignedBB bb, Color color, Color outline) {
        RenderUtil.renderBox(bb, color, outline, ((Float)((LagOMeter)this.module).lineWidth.getValue()).floatValue());
    }

    private Color getColor(Color c, double factor) {
        if (factor == 1.0) {
            return c;
        }
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)MathUtil.clamp((double)c.getAlpha() * factor, 0.0, 255.0));
    }
}
