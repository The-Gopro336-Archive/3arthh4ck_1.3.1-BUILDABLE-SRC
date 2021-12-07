/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketExplosion
 */
package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion
extends ModuleListener<BlockLag, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(BlockLag module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (((BlockLag)this.module).scaleExplosion.getValue().booleanValue()) {
            ((BlockLag)this.module).motionY = ((SPacketExplosion)event.getPacket()).getMotionY();
            ((BlockLag)this.module).scaleTimer.reset();
        }
    }
}

