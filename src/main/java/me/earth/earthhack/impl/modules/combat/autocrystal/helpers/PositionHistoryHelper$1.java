package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerPostListener;
import net.minecraft.network.play.client.CPacketPlayer;

class PositionHistoryHelper$1
extends CPacketPlayerPostListener {
    PositionHistoryHelper$1() {
    }

    @Override
    protected void onPacket(PacketEvent.Post<CPacketPlayer> event) {
        PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Post<CPacketPlayer.Position> event) {
        PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Post<CPacketPlayer.Rotation> event) {
        PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
        PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
    }
}
