package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket
extends CPacketPlayerListener {
    private final BowKiller module;

    public ListenerCPacket(BowKiller module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.module.onPacket(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.module.onPacket(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        if (this.module.cancelRotate.getValue().booleanValue()) {
            this.module.onPacket(event);
        }
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.module.onPacket(event);
    }
}
