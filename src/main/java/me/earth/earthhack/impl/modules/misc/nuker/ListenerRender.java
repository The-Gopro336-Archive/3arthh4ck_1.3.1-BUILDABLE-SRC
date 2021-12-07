/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.misc.nuker;

import java.util.Set;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Nuker, Render3DEvent> {
    public ListenerRender(Nuker module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!((Nuker)this.module).render.getValue().booleanValue() || !((Nuker)this.module).nuke.getValue().booleanValue()) {
            return;
        }
        RayTraceResult result = ListenerRender.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            Set<BlockPos> positions = ((Nuker)this.module).getSelection(result.getBlockPos());
            if (!positions.isEmpty()) {
                GL11.glPushMatrix();
                GL11.glPushAttrib((int)1048575);
            }
            for (BlockPos pos : positions) {
                AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
                RenderUtil.startRender();
                RenderUtil.drawBox(bb, ((Nuker)this.module).color.getValue());
                RenderUtil.endRender();
            }
            if (!positions.isEmpty()) {
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }
}

