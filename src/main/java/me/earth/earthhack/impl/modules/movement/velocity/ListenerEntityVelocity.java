/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityVelocity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerEntityVelocity
extends ModuleListener<Velocity, PacketEvent.Receive<SPacketEntityVelocity>> {
    public ListenerEntityVelocity(Velocity module) {
        super(module, PacketEvent.Receive.class, -1000000, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event) {
        ISPacketEntityVelocity velocity;
        if (((Velocity)this.module).knockBack.getValue().booleanValue() && ListenerEntityVelocity.mc.player != null && (velocity = (ISPacketEntityVelocity)event.getPacket()).getEntityID() == ListenerEntityVelocity.mc.player.getEntityId()) {
            if (((Velocity)this.module).horizontal.getValue().floatValue() == 0.0f && ((Velocity)this.module).vertical.getValue().floatValue() == 0.0f) {
                event.setCancelled(true);
            } else {
                velocity.setX((int)((float)velocity.getX() * ((Velocity)this.module).horizontal.getValue().floatValue()));
                velocity.setX((int)((float)velocity.getX() * ((Velocity)this.module).vertical.getValue().floatValue()));
                velocity.setX((int)((float)velocity.getX() * ((Velocity)this.module).horizontal.getValue().floatValue()));
            }
        }
    }
}

