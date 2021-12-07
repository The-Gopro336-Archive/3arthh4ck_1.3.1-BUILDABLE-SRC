/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket
extends CPacketPlayerListener {
    private final PacketFly packetFly;

    public ListenerCPacket(PacketFly packetFly) {
        this.packetFly = packetFly;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.packetFly.onPacketSend(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.packetFly.onPacketSend(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.packetFly.onPacketSend(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.packetFly.onPacketSend(event);
    }
}

