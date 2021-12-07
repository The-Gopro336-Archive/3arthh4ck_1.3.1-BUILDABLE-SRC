package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

class NoMotionUpdateService$2
extends CPacketPlayerListener {
    NoMotionUpdateService$2() {
    }

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
}
