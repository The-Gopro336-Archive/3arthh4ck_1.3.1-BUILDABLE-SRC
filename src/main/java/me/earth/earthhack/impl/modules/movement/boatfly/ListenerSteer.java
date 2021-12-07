package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.client.CPacketSteerBoat;

final class ListenerSteer
extends ModuleListener<BoatFly, PacketEvent.Send<CPacketSteerBoat>> {
    public ListenerSteer(BoatFly module) {
        super(module, PacketEvent.Send.class, CPacketSteerBoat.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketSteerBoat> event) {
        if (((BoatFly)this.module).noSteer.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}
