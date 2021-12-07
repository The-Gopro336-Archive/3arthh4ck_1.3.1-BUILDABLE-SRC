package me.earth.earthhack.impl.modules.client.server;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolPlayUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket
extends CPacketPlayerListener {
    private final ServerModule module;

    public ListenerCPacket(ServerModule module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.onEvent(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.onEvent(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.onEvent(event);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onEvent(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (event.isCancelled() || !this.module.sync.getValue().booleanValue()) {
            return;
        }
        if (this.module.currentMode == ServerMode.Client) {
            event.setCancelled(true);
            return;
        }
        Object p = event.getPacket();
        PacketBuffer buffer = null;
        try {
            buffer = new PacketBuffer(Unpooled.buffer());
            int id = EnumConnectionState.getFromPacket(p).getPacketId(EnumPacketDirection.SERVERBOUND, (Packet)p);
            buffer.writeInt(3);
            int index = buffer.writerIndex();
            buffer.writeInt(-1);
            int size = buffer.writerIndex();
            buffer.writeVarInt(id);
            ((CPacketPlayer)event.getPacket()).writePacketData(buffer);
            int lastIndex = buffer.writerIndex();
            size = buffer.writerIndex() - size;
            buffer.writerIndex(index);
            buffer.writeInt(size);
            buffer.writerIndex(lastIndex);
            byte[] packets = ProtocolPlayUtil.velocityAndPosition(RotationUtil.getRotationPlayer());
            for (IConnection connection : this.module.connectionManager.getConnections()) {
                try {
                    buffer.getBytes(0, connection.getOutputStream(), buffer.readableBytes());
                    connection.send(packets);
                }
                catch (IOException e) {
                    this.module.connectionManager.remove(connection);
                    Earthhack.getLogger().warn("Error with Connection: " + connection.getName());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (buffer != null) {
                BufferUtil.releaseBuffer(buffer);
            }
        }
    }
}
