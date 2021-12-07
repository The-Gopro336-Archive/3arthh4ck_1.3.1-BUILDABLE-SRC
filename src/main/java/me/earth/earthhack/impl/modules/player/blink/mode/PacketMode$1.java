package me.earth.earthhack.impl.modules.player.blink.mode;

import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import net.minecraft.network.Packet;

final class PacketMode$1
extends PacketMode {
    @Override
    public boolean shouldCancel(Packet<?> packet) {
        return true;
    }
}
