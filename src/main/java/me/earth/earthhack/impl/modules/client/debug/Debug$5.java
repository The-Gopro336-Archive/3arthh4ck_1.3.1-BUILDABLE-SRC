package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

class Debug$5
extends CPacketPlayerListener {
    Debug$5() {
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
    }
}
