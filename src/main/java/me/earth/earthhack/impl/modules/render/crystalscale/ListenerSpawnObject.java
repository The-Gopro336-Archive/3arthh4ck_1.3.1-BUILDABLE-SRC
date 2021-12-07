/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSpawnObject
 */
package me.earth.earthhack.impl.modules.render.crystalscale;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.crystalscale.CrystalScale;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.network.play.server.SPacketSpawnObject;

final class ListenerSpawnObject
extends ModuleListener<CrystalScale, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(CrystalScale module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (((SPacketSpawnObject)event.getPacket()).getType() == 51) {
            ((CrystalScale)this.module).scaleMap.put(((SPacketSpawnObject)event.getPacket()).getEntityID(), new TimeAnimation(((CrystalScale)this.module).time.getValue().intValue(), 0.1f, ((CrystalScale)this.module).scale.getValue().floatValue(), false, AnimationMode.LINEAR));
        }
    }
}

