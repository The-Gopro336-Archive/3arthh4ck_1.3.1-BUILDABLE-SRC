package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPacket
extends CPacketPlayerListener
implements Globals {
    private final Flight module;

    public ListenerPlayerPacket(Flight module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.onCPacket(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.onCPacket(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.onCPacket(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.onCPacket(event);
    }

    private void onCPacket(PacketEvent.Send<? extends CPacketPlayer> packet) {
        if (this.module.mode.getValue() == FlightMode.AAC) {
            if (ListenerPlayerPacket.mc.player.fallDistance > 3.8f) {
                ((ICPacketPlayer)packet.getPacket()).setOnGround(true);
                ListenerPlayerPacket.mc.player.fallDistance = 0.0f;
            }
        } else if (this.module.mode.getValue() == FlightMode.ConstantiamNew && this.module.constNewStage == 0) {
            packet.setCancelled(true);
        }
        if (this.module.mode.getValue() != FlightMode.Constantiam || this.module.clipped) {
            // empty if block
        }
    }
}
