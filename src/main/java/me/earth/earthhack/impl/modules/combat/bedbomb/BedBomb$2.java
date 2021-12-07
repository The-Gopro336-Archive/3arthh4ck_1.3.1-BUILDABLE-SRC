package me.earth.earthhack.impl.modules.combat.bedbomb;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

class BedBomb$2
extends CPacketPlayerListener {
    BedBomb$2() {
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        BedBomb.this.onPacket((CPacketPlayer)event.getPacket());
    }
}
