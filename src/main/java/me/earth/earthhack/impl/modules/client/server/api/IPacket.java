package me.earth.earthhack.impl.modules.client.server.api;

public interface IPacket {
    public int getId();

    public byte[] getBuffer();
}
