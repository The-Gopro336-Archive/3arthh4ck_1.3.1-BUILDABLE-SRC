/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntity$S15PacketEntityRelMove
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.world.World;

final class ListenerEntityRelativeMove
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove>> {
    public ListenerEntityRelativeMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S15PacketEntityRelMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
        if (ListenerEntityRelativeMove.mc.player.getRidingEntity() != null && ((BoatFly)this.module).noForceBoatMove.getValue().booleanValue() && ((SPacketEntity.S15PacketEntityRelMove)event.getPacket()).getEntity((World)ListenerEntityRelativeMove.mc.world) == ListenerEntityRelativeMove.mc.player.getRidingEntity()) {
            event.setCancelled(true);
        }
    }
}

