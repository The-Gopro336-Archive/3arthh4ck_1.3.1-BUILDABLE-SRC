package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketEntity;

final class ListenerEntityLookMove
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>> {
    public ListenerEntityLookMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S17PacketEntityLookMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
        if (ListenerEntityLookMove.mc.player.getRidingEntity() != null && ((BoatFly)this.module).noForceBoatMove.getValue().booleanValue() && ((SPacketEntity.S17PacketEntityLookMove)event.getPacket()).func_149065_a(ListenerEntityLookMove.mc.world) == ListenerEntityLookMove.mc.player.getRidingEntity()) {
            event.setCancelled(true);
        }
    }
}
