package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

class PositionManager$2
extends EventListener<PacketEvent.Post<CPacketPlayer.Position>> {
    PositionManager$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayer.Position> event) {
        PositionManager.this.readCPacket((CPacketPlayer)event.getPacket());
    }
}
