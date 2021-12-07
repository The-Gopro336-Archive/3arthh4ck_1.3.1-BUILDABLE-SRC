/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketUseEntity
 */
package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import net.minecraft.network.play.client.CPacketUseEntity;

final class ListenerAttack
extends ModuleListener<Spectate, PacketEvent.Send<CPacketUseEntity>> {
    public ListenerAttack(Spectate module) {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event) {
        if (((ICPacketUseEntity)event.getPacket()).getEntityID() == ListenerAttack.mc.player.getEntityId()) {
            event.setCancelled(true);
        }
    }
}

