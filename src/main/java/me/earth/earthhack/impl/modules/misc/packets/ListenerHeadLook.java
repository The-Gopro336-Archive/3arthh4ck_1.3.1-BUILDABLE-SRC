/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketEntityHeadLook
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityHeadLook;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityHeadLook;

final class ListenerHeadLook
extends ModuleListener<Packets, PacketEvent.Receive<SPacketEntityHeadLook>> {
    public ListenerHeadLook(Packets module) {
        super(module, PacketEvent.Receive.class, SPacketEntityHeadLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityHeadLook> event) {
        ISPacketEntityHeadLook p;
        Entity entity;
        if (((Packets)this.module).fastHeadLook.getValue().booleanValue() && !event.isCancelled() && (entity = Managers.ENTITIES.getEntity((p = (ISPacketEntityHeadLook)event.getPacket()).getEntityId())) != null) {
            entity.setRotationYawHead((float)(((SPacketEntityHeadLook)event.getPacket()).getYaw() * 360) / 256.0f);
        }
    }
}

