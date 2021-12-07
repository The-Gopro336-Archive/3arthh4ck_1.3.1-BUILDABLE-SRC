/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketVehicleMove
 */
package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;

final class ListenerPacket
extends ModuleListener<Freecam, PacketEvent.Send<?>> {
    public ListenerPacket(Freecam module) {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event) {
        switch (((Freecam)this.module).mode.getValue()) {
            case Cancel: {
                if (!(event.getPacket() instanceof CPacketPlayer)) break;
                event.setCancelled(true);
                break;
            }
            case Spanish: {
                Object packet = event.getPacket();
                if (packet instanceof CPacketUseEntity || packet instanceof CPacketPlayerTryUseItem || packet instanceof CPacketPlayerTryUseItemOnBlock || packet instanceof CPacketPlayer || packet instanceof CPacketVehicleMove || packet instanceof CPacketChatMessage || packet instanceof CPacketKeepAlive) break;
                event.setCancelled(true);
                break;
            }
        }
    }
}

