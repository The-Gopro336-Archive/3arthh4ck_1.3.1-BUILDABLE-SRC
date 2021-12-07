package me.earth.earthhack.impl.modules.player.blink.mode;

import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketTabComplete;

final class PacketMode$3
extends PacketMode {
    @Override
    public boolean shouldCancel(Packet<?> packet) {
        return !(packet instanceof CPacketChatMessage) && !(packet instanceof CPacketConfirmTeleport) && !(packet instanceof CPacketKeepAlive) && !(packet instanceof CPacketTabComplete) && !(packet instanceof CPacketClientStatus);
    }
}
