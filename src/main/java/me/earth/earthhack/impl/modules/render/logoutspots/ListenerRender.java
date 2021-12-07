/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.earthhack.impl.modules.render.logoutspots;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerRender
extends ModuleListener<LogoutSpots, Render3DEvent> {
    public ListenerRender(LogoutSpots module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (((LogoutSpots)this.module).render.getValue().booleanValue()) {
            for (LogoutSpot spot : ((LogoutSpots)this.module).spots.values()) {
                AxisAlignedBB bb = Interpolation.interpolateAxis(spot.getBoundingBox());
                RenderUtil.startRender();
                RenderUtil.drawOutline(bb, 1.5f, Color.RED);
                RenderUtil.endRender();
                String text = "\u00a7c" + spot.getName() + " XYZ : " + MathUtil.round(spot.getX(), 1) + ", " + MathUtil.round(spot.getY(), 1) + ", " + MathUtil.round(spot.getZ(), 1) + " (" + MathUtil.round(spot.getDistance(), 1) + ")";
                RenderUtil.drawNametag(text, bb, ((LogoutSpots)this.module).scale.getValue().floatValue(), -65536);
            }
        }
    }
}

