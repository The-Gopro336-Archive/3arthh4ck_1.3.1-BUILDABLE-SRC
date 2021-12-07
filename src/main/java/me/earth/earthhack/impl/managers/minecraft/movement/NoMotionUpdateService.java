/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoMotionUpdateService
extends SubscriberImpl {
    private boolean awaiting;

    public NoMotionUpdateService() {
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MIN_VALUE){

            @Override
            public void invoke(MotionUpdateEvent event) {
                if (event.isCancelled()) {
                    return;
                }
                if (event.getStage() == Stage.PRE) {
                    NoMotionUpdateService.this.setAwaiting(true);
                } else {
                    if (NoMotionUpdateService.this.isAwaiting()) {
                        Bus.EVENT_BUS.post(new NoMotionUpdateEvent());
                    }
                    NoMotionUpdateService.this.setAwaiting(false);
                }
            }
        });
        this.listeners.addAll(new CPacketPlayerListener(){

            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
                NoMotionUpdateService.this.setAwaiting(false);
            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
                NoMotionUpdateService.this.setAwaiting(false);
            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
                NoMotionUpdateService.this.setAwaiting(false);
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
                NoMotionUpdateService.this.setAwaiting(false);
            }
        }.getListeners());
    }

    public void setAwaiting(boolean awaiting) {
        this.awaiting = awaiting;
    }

    public boolean isAwaiting() {
        return this.awaiting;
    }
}

