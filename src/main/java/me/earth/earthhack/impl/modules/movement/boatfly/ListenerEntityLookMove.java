/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntity$S17PacketEntityLookMove
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.world.World;

final class ListenerEntityLookMove
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>> {
    public ListenerEntityLookMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S17PacketEntityLookMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
        if (ListenerEntityLookMove.mc.player.getRidingEntity() != null && ((BoatFly)this.module).noForceBoatMove.getValue().booleanValue() && ((SPacketEntity.S17PacketEntityLookMove)event.getPacket()).getEntity((World)ListenerEntityLookMove.mc.world) == ListenerEntityLookMove.mc.player.getRidingEntity()) {
            event.setCancelled(true);
        }
    }
}

