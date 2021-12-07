package me.earth.earthhack.impl.modules.render.breadcrumbs;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import net.minecraft.util.math.Vec3d;

final class ListenerTick
extends ModuleListener<BreadCrumbs, TickEvent> {
    public ListenerTick(BreadCrumbs module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe() && ((BreadCrumbs)this.module).timer.passed(((BreadCrumbs)this.module).delay.getValue().intValue())) {
            List<Trace.TracePos> trace;
            Vec3d vec3d;
            Vec3d vec = ListenerTick.mc.player.getPositionVector();
            if (vec.equals(BreadCrumbs.ORIGIN)) {
                return;
            }
            if (((BreadCrumbs)this.module).trace == null) {
                ((BreadCrumbs)this.module).trace = new Trace(0, null, ListenerTick.mc.world.provider.getDimensionType(), vec, new ArrayList<Trace.TracePos>());
            }
            Vec3d vec3d2 = vec3d = (trace = ((BreadCrumbs)this.module).trace.getTrace()).isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
            if (!trace.isEmpty() && (vec.distanceTo(vec3d) > 100.0 || ((BreadCrumbs)this.module).trace.getType() != ListenerTick.mc.world.provider.getDimensionType())) {
                ((BreadCrumbs)this.module).positions.add(((BreadCrumbs)this.module).trace);
                trace = new ArrayList<Trace.TracePos>();
                ((BreadCrumbs)this.module).trace = new Trace(((BreadCrumbs)this.module).positions.size() + 1, null, ListenerTick.mc.world.provider.getDimensionType(), vec, trace);
            }
            if (trace.isEmpty() || !vec.equals(vec3d)) {
                trace.add(new Trace.TracePos(vec, System.currentTimeMillis() + (long)((BreadCrumbs)this.module).fadeDelay.getValue().intValue()));
            }
            ((BreadCrumbs)this.module).timer.reset();
        }
    }
}
