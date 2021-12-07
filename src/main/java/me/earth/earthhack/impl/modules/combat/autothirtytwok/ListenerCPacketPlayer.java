package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacketPlayer
extends CPacketPlayerListener {
    private final Auto32k module;

    public ListenerCPacketPlayer(Auto32k module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.module.onCPacketPlayer((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.module.onCPacketPlayer((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.module.onCPacketPlayer((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.module.onCPacketPlayer((CPacketPlayer)event.getPacket());
    }
}
