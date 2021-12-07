/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.play.server.SPacketCustomPayload
 */
package me.earth.earthhack.impl.modules.client.pingbypass.packets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadReader;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;

public class PayloadManager {
    private final Map<Short, PayloadReader> readers = new ConcurrentHashMap<Short, PayloadReader>();

    public void onPacket(SPacketCustomPayload packet) {
        short id;
        PacketBuffer buffer = packet.getBufferData();
        try {
            id = buffer.readShort();
        }
        catch (Exception e) {
            Earthhack.getLogger().error("Could not read id from PayloadPacket.");
            return;
        }
        PayloadReader reader = this.readers.get(id);
        if (reader == null) {
            Earthhack.getLogger().error("Couldn't find PayloadReader for ID: " + id);
            return;
        }
        try {
            reader.read(buffer);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void register(short id, PayloadReader reader) {
        this.readers.compute(id, (i, v) -> v == null ? reader : v.compose(reader));
    }
}

