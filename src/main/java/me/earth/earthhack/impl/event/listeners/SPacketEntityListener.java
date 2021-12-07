/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntity
 *  net.minecraft.network.play.server.SPacketEntity$S15PacketEntityRelMove
 *  net.minecraft.network.play.server.SPacketEntity$S16PacketEntityLook
 *  net.minecraft.network.play.server.SPacketEntity$S17PacketEntityLookMove
 */
package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntity;

public abstract class SPacketEntityListener
extends SubscriberImpl
implements Globals {
    public SPacketEntityListener() {
        this(10);
    }

    public SPacketEntityListener(int priority) {
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketEntity>>(PacketEvent.Receive.class, priority, SPacketEntity.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity> event) {
                SPacketEntityListener.this.onPacket(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove>>(PacketEvent.Receive.class, priority, SPacketEntity.S15PacketEntityRelMove.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
                SPacketEntityListener.this.onPosition(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketEntity.S16PacketEntityLook>>(PacketEvent.Receive.class, priority, SPacketEntity.S16PacketEntityLook.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) {
                SPacketEntityListener.this.onRotation(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>>(PacketEvent.Receive.class, priority, SPacketEntity.S17PacketEntityLookMove.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
                SPacketEntityListener.this.onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket(PacketEvent.Receive<SPacketEntity> var1);

    protected abstract void onPosition(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> var1);

    protected abstract void onRotation(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> var1);

    protected abstract void onPositionRotation(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> var1);
}

