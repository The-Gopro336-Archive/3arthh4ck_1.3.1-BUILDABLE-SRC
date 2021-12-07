package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketEntity;

final class ListenerEntityLook
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S16PacketEntityLook>> {
    public ListenerEntityLook(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S16PacketEntityLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) {
        if (ListenerEntityLook.mc.player.getRidingEntity() != null && ((BoatFly)this.module).noForceBoatMove.getValue().booleanValue() && ((SPacketEntity.S16PacketEntityLook)event.getPacket()).func_149065_a(ListenerEntityLook.mc.world) == ListenerEntityLook.mc.player.getRidingEntity()) {
            event.setCancelled(true);
        }
    }
}
