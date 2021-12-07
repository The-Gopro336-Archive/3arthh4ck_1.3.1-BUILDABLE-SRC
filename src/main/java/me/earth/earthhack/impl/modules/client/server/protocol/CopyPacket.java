/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.EnumConnectionState
 *  net.minecraft.network.Packet
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.play.INetHandlerPlayServer
 */
package me.earth.earthhack.impl.modules.client.server.protocol;

import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CopyPacket
implements Packet<INetHandlerPlayServer>,
CustomPacket {
    private final byte[] buffer;
    private final int ordinal;
    private final int offset;
    private final int id;

    public CopyPacket(int id, int ordinal, byte[] buffer) {
        this(id, ordinal, buffer, 0);
    }

    public CopyPacket(int id, int ordinal, byte[] buffer, int offset) {
        this.id = id;
        this.ordinal = ordinal;
        this.buffer = buffer;
        this.offset = offset;
    }

    @Override
    public int getId() throws Exception {
        return this.id;
    }

    @Override
    public EnumConnectionState getState() {
        return EnumConnectionState.values()[this.ordinal];
    }

    public void readPacketData(PacketBuffer buf) {
        throw new UnsupportedOperationException();
    }

    public void writePacketData(PacketBuffer buf) {
        buf.writeBytes(this.buffer, this.offset, this.buffer.length - this.offset);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        throw new UnsupportedOperationException();
    }
}

