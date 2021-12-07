package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketMoveVehicle;

final class ListenerServerVehicleMove
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketMoveVehicle>> {
    public ListenerServerVehicleMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketMoveVehicle.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMoveVehicle> event) {
        if (((BoatFly)this.module).noVehicleMove.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}
