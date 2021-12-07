package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketSetPassengers;

final class ListenerDismount
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketSetPassengers>> {
    public ListenerDismount(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketSetPassengers.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetPassengers> event) {
        EntityPlayerSP player = ListenerDismount.mc.player;
        if (player == null) {
            return;
        }
        Entity riding = ListenerDismount.mc.player.getRidingEntity();
        if (riding != null && ((SPacketSetPassengers)event.getPacket()).getEntityId() == riding.getEntityId() && ((BoatFly)this.module).remount.getValue().booleanValue()) {
            event.setCancelled(true);
            if (((BoatFly)this.module).schedule.getValue().booleanValue()) {
                mc.addScheduledTask(() -> this.remove((SPacketSetPassengers)event.getPacket(), player, riding));
            } else {
                this.remove((SPacketSetPassengers)event.getPacket(), player, riding);
            }
        }
    }

    private void remove(SPacketSetPassengers packet, Entity player, Entity riding) {
        for (int id : packet.getPassengerIds()) {
            if (id == player.getEntityId()) {
                if (!((BoatFly)this.module).remountPackets.getValue().booleanValue()) continue;
                ((BoatFly)this.module).sendPackets(riding);
                continue;
            }
            try {
                Entity entity = ListenerDismount.mc.world.getEntityByID(id);
                if (entity == null) continue;
                entity.dismountRidingEntity();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
