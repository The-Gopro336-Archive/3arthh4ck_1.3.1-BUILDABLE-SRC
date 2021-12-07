/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.render.trails;

import java.util.List;
import java.util.Map;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Trails, Render3DEvent> {
    public ListenerRender(Trails module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        for (Map.Entry<Integer, List<Trace>> entry : ((Trails)this.module).traceLists.entrySet()) {
            RenderUtil.startRender();
            GL11.glLineWidth((float)((Trails)this.module).width.getValue().floatValue());
            TimeAnimation animation = ((Trails)this.module).ids.get(entry.getKey());
            animation.add(event.getPartialTicks());
            GL11.glColor4f((float)((Trails)this.module).color.getR(), (float)((Trails)this.module).color.getG(), (float)((Trails)this.module).color.getB(), (float)MathHelper.clamp((float)((float)((double)((Trails)this.module).color.getA() - animation.getCurrent() / 255.0)), (float)0.0f, (float)255.0f));
            entry.getValue().forEach(trace -> {
                GL11.glBegin((int)3);
                trace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
            });
            GL11.glColor4f((float)((Trails)this.module).color.getR(), (float)((Trails)this.module).color.getG(), (float)((Trails)this.module).color.getB(), (float)MathHelper.clamp((float)((float)((double)((Trails)this.module).color.getA() - animation.getCurrent() / 255.0)), (float)0.0f, (float)255.0f));
            GL11.glBegin((int)3);
            Trace trace2 = ((Trails)this.module).traces.get(entry.getKey());
            if (trace2 != null) {
                trace2.getTrace().forEach(this::renderVec);
            }
            GL11.glEnd();
            RenderUtil.endRender();
        }
    }

    private void renderVec(Trace.TracePos tracePos) {
        double x = tracePos.getPos().x - Interpolation.getRenderPosX();
        double y = tracePos.getPos().y - Interpolation.getRenderPosY();
        double z = tracePos.getPos().z - Interpolation.getRenderPosZ();
        GL11.glVertex3d((double)x, (double)y, (double)z);
    }
}

