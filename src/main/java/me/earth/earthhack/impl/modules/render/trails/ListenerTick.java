/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.render.trails;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.Vec3d;

final class ListenerTick
extends ModuleListener<Trails, TickEvent> {
    public ListenerTick(Trails module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.world == null) {
            return;
        }
        if (((Trails)this.module).ids.keySet().isEmpty()) {
            return;
        }
        for (Integer id : ((Trails)this.module).ids.keySet()) {
            if (id == null) continue;
            if (ListenerTick.mc.world.loadedEntityList == null) {
                return;
            }
            if (ListenerTick.mc.world.loadedEntityList.isEmpty()) {
                return;
            }
            Trace idTrace = ((Trails)this.module).traces.get(id);
            Entity entity = ListenerTick.mc.world.getEntityByID(id.intValue());
            if (entity != null) {
                List<Trace.TracePos> trace;
                Vec3d vec3d;
                Vec3d vec = entity.getPositionVector();
                if (vec == null || vec.equals((Object)BreadCrumbs.ORIGIN)) continue;
                if (!((Trails)this.module).traces.containsKey(id) || idTrace == null) {
                    ((Trails)this.module).traces.put(id, new Trace(0, null, ListenerTick.mc.world.provider.getDimensionType(), vec, new ArrayList<Trace.TracePos>()));
                    idTrace = ((Trails)this.module).traces.get(id);
                }
                Vec3d vec3d2 = vec3d = (trace = idTrace.getTrace()).isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
                if (!trace.isEmpty() && (vec.distanceTo(vec3d) > 100.0 || idTrace.getType() != ListenerTick.mc.world.provider.getDimensionType())) {
                    ((Trails)this.module).traceLists.get(id).add(idTrace);
                    trace = new ArrayList<Trace.TracePos>();
                    ((Trails)this.module).traces.put(id, new Trace(((Trails)this.module).traceLists.get(id).size() + 1, null, ListenerTick.mc.world.provider.getDimensionType(), vec, new ArrayList<Trace.TracePos>()));
                }
                if (trace.isEmpty() || !vec.equals((Object)vec3d)) {
                    trace.add(new Trace.TracePos(vec));
                }
            }
            TimeAnimation animation = ((Trails)this.module).ids.get(id);
            if (entity instanceof EntityArrow && (entity.onGround || entity.collided || !entity.isAirBorne)) {
                animation.play();
            }
            if (animation == null || !((double)((Trails)this.module).color.getAlpha() - animation.getCurrent() <= 0.0)) continue;
            animation.stop();
            ((Trails)this.module).ids.remove(id);
            ((Trails)this.module).traceLists.remove(id);
            ((Trails)this.module).traces.remove(id);
        }
    }
}

