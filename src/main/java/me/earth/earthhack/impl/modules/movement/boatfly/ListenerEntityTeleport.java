package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketEntityTeleport;

final class ListenerEntityTeleport
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntityTeleport>> {
    public ListenerEntityTeleport(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntityTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityTeleport> event) {
        if (ListenerEntityTeleport.mc.player.getRidingEntity() != null && ((BoatFly)this.module).noForceBoatMove.getValue().booleanValue() && ((SPacketEntityTeleport)event.getPacket()).getEntityId() == ListenerEntityTeleport.mc.player.getRidingEntity().getEntityId()) {
            event.setCancelled(true);
        }
    }
}
