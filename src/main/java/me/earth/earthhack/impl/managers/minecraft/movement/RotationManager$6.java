package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

class RotationManager$6
extends EventListener<PacketEvent.Post<CPacketPlayer.PositionRotation>> {
    RotationManager$6(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
        RotationManager.this.readCPacket((CPacketPlayer)event.getPacket());
    }
}
