/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity
extends ModuleListener<BlockLag, PacketEvent.Receive<SPacketEntityVelocity>> {
    public ListenerVelocity(BlockLag module) {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event) {
        if (!((BlockLag)this.module).scaleVelocity.getValue().booleanValue()) {
            return;
        }
        EntityPlayerSP playerSP = ListenerVelocity.mc.player;
        if (playerSP != null && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == playerSP.getEntityId()) {
            ((BlockLag)this.module).motionY = (double)((SPacketEntityVelocity)event.getPacket()).getMotionY() / 8000.0;
            ((BlockLag)this.module).scaleTimer.reset();
        }
    }
}

