package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<BreadCrumbs, Render3DEvent> {
    public ListenerRender(BreadCrumbs module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (((BreadCrumbs)this.module).render.getValue().booleanValue() && ((BreadCrumbs)this.module).trace != null) {
            RenderUtil.startRender();
            GL11.glLineWidth(((BreadCrumbs)this.module).width.getValue().floatValue());
            ((BreadCrumbs)this.module).positions.forEach(trace -> {
                GL11.glBegin(3);
                trace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
                if (((BreadCrumbs)this.module).fade.getValue().booleanValue()) {
                    trace.getTrace().removeIf(Trace.TracePos::shouldRemoveTrace);
                }
            });
            GL11.glBegin(3);
            ((BreadCrumbs)this.module).trace.getTrace().forEach(this::renderVec);
            if (((BreadCrumbs)this.module).fade.getValue().booleanValue()) {
                ((BreadCrumbs)this.module).trace.getTrace().removeIf(Trace.TracePos::shouldRemoveTrace);
            }
            GL11.glEnd();
            RenderUtil.endRender();
        }
    }

    private void renderVec(Trace.TracePos tracePos) {
        double x = tracePos.getPos().x - Interpolation.getRenderPosX();
        double y = tracePos.getPos().y - Interpolation.getRenderPosY();
        double z = tracePos.getPos().z - Interpolation.getRenderPosZ();
        float percentage = ((BreadCrumbs)this.module).fade.getValue() != false ? MathHelper.clamp((float)(((BreadCrumbs)this.module).color.getA() / (float)((BreadCrumbs)this.module).fadeDelay.getValue().intValue() * (float)(tracePos.getTime() - System.currentTimeMillis())), (float)0.0f, (float)((BreadCrumbs)this.module).color.getB()) / ((BreadCrumbs)this.module).color.getB() : ((BreadCrumbs)this.module).color.getA();
        GL11.glColor4f(((BreadCrumbs)this.module).color.getR(), ((BreadCrumbs)this.module).color.getG(), ((BreadCrumbs)this.module).color.getB(), percentage);
        GL11.glVertex3d(x, y, z);
    }
}
