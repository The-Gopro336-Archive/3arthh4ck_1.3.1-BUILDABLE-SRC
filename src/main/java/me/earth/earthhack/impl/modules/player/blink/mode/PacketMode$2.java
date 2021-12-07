package me.earth.earthhack.impl.modules.player.blink.mode;

import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

final class PacketMode$2
extends PacketMode {
    @Override
    public boolean shouldCancel(Packet<?> packet) {
        return packet instanceof CPacketPlayer;
    }
}
