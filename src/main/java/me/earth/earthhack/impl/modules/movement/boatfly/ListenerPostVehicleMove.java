package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;

final class ListenerPostVehicleMove
extends ModuleListener<BoatFly, PacketEvent.Post<CPacketVehicleMove>> {
    public ListenerPostVehicleMove(BoatFly module) {
        super(module, PacketEvent.Post.class, CPacketVehicleMove.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketVehicleMove> event) {
        Entity riding = ListenerPostVehicleMove.mc.player.getRidingEntity();
        if (riding != null && !((BoatFly)this.module).packetSet.contains(event.getPacket()) && ((BoatFly)this.module).bypass.getValue().booleanValue() && ((BoatFly)this.module).postBypass.getValue().booleanValue() && ((BoatFly)this.module).tickCount++ >= ((BoatFly)this.module).ticks.getValue()) {
            for (int i = 0; i <= ((BoatFly)this.module).packets.getValue(); ++i) {
                ((BoatFly)this.module).sendPackets(riding);
            }
            ((BoatFly)this.module).tickCount = 0;
        }
    }
}
