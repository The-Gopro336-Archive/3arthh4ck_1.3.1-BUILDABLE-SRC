/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity
extends ModuleListener<Packets, PacketEvent.Receive<SPacketEntityVelocity>> {
    public ListenerVelocity(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event) {
        if (event.isCancelled() || !((Packets)this.module).fastVelocity.getValue().booleanValue()) {
            return;
        }
        SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
        Entity entity = Managers.ENTITIES.getEntity(packet.getEntityID());
        if (entity != null) {
            event.setCancelled(((Packets)this.module).cancelVelocity.getValue());
            entity.setVelocity((double)packet.getMotionX() / 8000.0, (double)packet.getMotionY() / 8000.0, (double)packet.getMotionZ() / 8000.0);
        }
    }
}

