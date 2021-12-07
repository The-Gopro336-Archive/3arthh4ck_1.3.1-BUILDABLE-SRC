/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

public abstract class CPacketPlayerPostListener
extends SubscriberImpl {
    public CPacketPlayerPostListener() {
        this(10);
    }

    public CPacketPlayerPostListener(int priority) {
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer>>(PacketEvent.Post.class, priority, CPacketPlayer.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer> event) {
                CPacketPlayerPostListener.this.onPacket(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.Position>>(PacketEvent.Post.class, priority, CPacketPlayer.Position.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Position> event) {
                CPacketPlayerPostListener.this.onPosition(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.Rotation>>(PacketEvent.Post.class, priority, CPacketPlayer.Rotation.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Rotation> event) {
                CPacketPlayerPostListener.this.onRotation(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.PositionRotation>>(PacketEvent.Post.class, priority, CPacketPlayer.PositionRotation.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
                CPacketPlayerPostListener.this.onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket(PacketEvent.Post<CPacketPlayer> var1);

    protected abstract void onPosition(PacketEvent.Post<CPacketPlayer.Position> var1);

    protected abstract void onRotation(PacketEvent.Post<CPacketPlayer.Rotation> var1);

    protected abstract void onPositionRotation(PacketEvent.Post<CPacketPlayer.PositionRotation> var1);
}

