/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketEntity
 *  net.minecraft.network.play.server.SPacketEntity$S15PacketEntityRelMove
 *  net.minecraft.network.play.server.SPacketEntity$S16PacketEntityLook
 *  net.minecraft.network.play.server.SPacketEntity$S17PacketEntityLookMove
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.SPacketEntityListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntity;

final class ListenerEntity
extends SPacketEntityListener {
    private final Packets packets;

    public ListenerEntity(Packets packets) {
        super(-2147483647);
        this.packets = packets;
    }

    @Override
    protected void onPacket(PacketEvent.Receive<SPacketEntity> event) {
        this.onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
        this.onEvent(event);
    }

    @Override
    protected void onRotation(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) {
        this.onEvent(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
        this.onEvent(event);
    }

    private void onEvent(PacketEvent.Receive<? extends SPacketEntity> event) {
        if (event.isCancelled() || !this.packets.fastEntities.getValue().booleanValue()) {
            return;
        }
        SPacketEntity packet = (SPacketEntity)event.getPacket();
        Entity e = Managers.ENTITIES.getEntity(((ISPacketEntity)packet).getEntityId());
        if (e == null) {
            return;
        }
        event.setCancelled(true);
        e.serverPosX += (long)packet.getX();
        e.serverPosY += (long)packet.getY();
        e.serverPosZ += (long)packet.getZ();
        double x = (double)e.serverPosX / 4096.0;
        double y = (double)e.serverPosY / 4096.0;
        double z = (double)e.serverPosZ / 4096.0;
        if (!e.canPassengerSteer()) {
            float yaw = packet.isRotating() ? (float)(packet.getYaw() * 360) / 256.0f : e.rotationYaw;
            float pitch = packet.isRotating() ? (float)(packet.getPitch() * 360) / 256.0f : e.rotationPitch;
            e.setPositionAndRotationDirect(x, y, z, yaw, pitch, 3, false);
            e.onGround = packet.getOnGround();
        }
    }
}

