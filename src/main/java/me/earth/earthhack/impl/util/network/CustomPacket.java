package me.earth.earthhack.impl.util.network;

import net.minecraft.network.EnumConnectionState;

public interface CustomPacket {
    public int getId() throws Exception;

    default public EnumConnectionState getState() {
        return EnumConnectionState.PLAY;
    }
}
