/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.network.INetHandler
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.INetHandlerPlayClient
 */
package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.INetworkManager;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;

public class NetworkUtil
implements Globals {
    public static void send(Packet<?> packet) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    public static Packet<?> sendPacketNoEvent(Packet<?> packet) {
        return NetworkUtil.sendPacketNoEvent(packet, true);
    }

    public static Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null) {
            INetworkManager manager = (INetworkManager)connection.getNetworkManager();
            return manager.sendPacketNoEvent(packet, post);
        }
        return null;
    }

    public static boolean receive(Packet<INetHandlerPlayClient> packet) {
        PacketEvent.Receive<Packet<INetHandlerPlayClient>> e = new PacketEvent.Receive<Packet<INetHandlerPlayClient>>(packet);
        Bus.EVENT_BUS.post(e, packet.getClass());
        if (e.isCancelled()) {
            return false;
        }
        packet.processPacket((INetHandler)NetworkUtil.mc.player.connection);
        for (Runnable runnable : e.getPostEvents()) {
            runnable.run();
        }
        return true;
    }
}

