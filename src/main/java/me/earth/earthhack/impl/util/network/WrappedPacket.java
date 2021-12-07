/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.EnumConnectionState
 *  net.minecraft.network.EnumPacketDirection
 *  net.minecraft.network.Packet
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.play.INetHandlerPlayServer
 */
package me.earth.earthhack.impl.util.network;

import java.io.IOException;
import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class WrappedPacket
implements Packet<INetHandlerPlayServer>,
CustomPacket {
    private final Packet<INetHandlerPlayServer> wrapped;

    public WrappedPacket(Packet<INetHandlerPlayServer> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getId() throws Exception {
        return this.getState().getPacketId(EnumPacketDirection.SERVERBOUND, this.wrapped);
    }

    @Override
    public EnumConnectionState getState() {
        return EnumConnectionState.getFromPacket(this.wrapped);
    }

    public void readPacketData(PacketBuffer buf) {
        throw new UnsupportedOperationException();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        this.wrapped.writePacketData(buf);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        throw new UnsupportedOperationException();
    }
}

