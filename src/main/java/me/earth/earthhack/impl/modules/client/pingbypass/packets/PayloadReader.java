/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketBuffer
 */
package me.earth.earthhack.impl.modules.client.pingbypass.packets;

import net.minecraft.network.PacketBuffer;

public interface PayloadReader {
    public void read(PacketBuffer var1);

    default public PayloadReader compose(PayloadReader reader) {
        return buffer -> {
            this.read(buffer);
            buffer.resetReaderIndex();
            reader.read(buffer);
        };
    }
}

