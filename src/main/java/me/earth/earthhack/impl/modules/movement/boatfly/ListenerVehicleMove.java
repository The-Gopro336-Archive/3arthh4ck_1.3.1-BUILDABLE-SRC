/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketVehicleMove
 */
package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;

final class ListenerVehicleMove
extends ModuleListener<BoatFly, PacketEvent.Send<CPacketVehicleMove>> {
    public ListenerVehicleMove(BoatFly module) {
        super(module, PacketEvent.Send.class, CPacketVehicleMove.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketVehicleMove> event) {
        Entity riding = ListenerVehicleMove.mc.player.getRidingEntity();
        if (ListenerVehicleMove.mc.player.getRidingEntity() != null && !((BoatFly)this.module).packetSet.contains(event.getPacket()) && ((BoatFly)this.module).bypass.getValue().booleanValue() && !((BoatFly)this.module).postBypass.getValue().booleanValue() && ((BoatFly)this.module).tickCount++ >= ((BoatFly)this.module).ticks.getValue()) {
            for (int i = 0; i <= ((BoatFly)this.module).packets.getValue(); ++i) {
                ((BoatFly)this.module).sendPackets(riding);
            }
            ((BoatFly)this.module).tickCount = 0;
        }
    }
}

