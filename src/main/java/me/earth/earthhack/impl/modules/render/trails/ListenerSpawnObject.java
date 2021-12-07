/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.render.trails;

import java.util.ArrayList;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.Vec3d;

final class ListenerSpawnObject
extends ModuleListener<Trails, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(Trails module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (((Trails)this.module).pearls.getValue() != false && ((SPacketSpawnObject)event.getPacket()).getType() == 65 || ((Trails)this.module).arrows.getValue() != false && ((SPacketSpawnObject)event.getPacket()).getType() == 60 || ((Trails)this.module).snowballs.getValue().booleanValue() && ((SPacketSpawnObject)event.getPacket()).getType() == 61) {
            Earthhack.getLogger().info((Object)((SPacketSpawnObject)event.getPacket()).getEntityID());
            TimeAnimation animation = new TimeAnimation(((Trails)this.module).time.getValue() * 1000, 0.0, ((Trails)this.module).color.getAlpha(), false, AnimationMode.LINEAR);
            animation.stop();
            ((Trails)this.module).ids.put(((SPacketSpawnObject)event.getPacket()).getEntityID(), animation);
            ((Trails)this.module).traceLists.put(((SPacketSpawnObject)event.getPacket()).getEntityID(), new ArrayList());
            ((Trails)this.module).traces.put(((SPacketSpawnObject)event.getPacket()).getEntityID(), new Trace(0, null, ListenerSpawnObject.mc.world.provider.getDimensionType(), new Vec3d(((SPacketSpawnObject)event.getPacket()).getX(), ((SPacketSpawnObject)event.getPacket()).getY(), ((SPacketSpawnObject)event.getPacket()).getZ()), new ArrayList<Trace.TracePos>()));
        }
    }
}

