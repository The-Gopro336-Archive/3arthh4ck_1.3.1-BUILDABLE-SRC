/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity
extends ModuleListener<HighJump, PacketEvent.Receive<SPacketEntityVelocity>> {
    public ListenerVelocity(HighJump module) {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event) {
        EntityPlayerSP player = ListenerVelocity.mc.player;
        SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
        if (((HighJump)this.module).velocity.getValue().booleanValue() && player != null && player.getEntityId() == packet.getEntityID()) {
            double y = (double)packet.getMotionY() / 8000.0;
            ((HighJump)this.module).addVelocity(y);
        }
    }
}

