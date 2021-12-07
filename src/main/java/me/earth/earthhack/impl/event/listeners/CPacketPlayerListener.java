package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

public abstract class CPacketPlayerListener
extends SubscriberImpl {
    public CPacketPlayerListener() {
        this(10);
    }

    public CPacketPlayerListener(int priority) {
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketPlayer>>(PacketEvent.Send.class, priority, CPacketPlayer.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer> event) {
                CPacketPlayerListener.this.onPacket(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketPlayer.Position>>(PacketEvent.Send.class, priority, CPacketPlayer.Position.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer.Position> event) {
                CPacketPlayerListener.this.onPosition(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketPlayer.Rotation>>(PacketEvent.Send.class, priority, CPacketPlayer.Rotation.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer.Rotation> event) {
                CPacketPlayerListener.this.onRotation(event);
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketPlayer.PositionRotation>>(PacketEvent.Send.class, priority, CPacketPlayer.PositionRotation.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
                CPacketPlayerListener.this.onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket(PacketEvent.Send<CPacketPlayer> var1);

    protected abstract void onPosition(PacketEvent.Send<CPacketPlayer.Position> var1);

    protected abstract void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> var1);

    protected abstract void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> var1);
}
