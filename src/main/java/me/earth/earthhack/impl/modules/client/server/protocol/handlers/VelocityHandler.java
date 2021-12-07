/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.api.IVelocityHandler;

public class VelocityHandler
implements IPacketHandler {
    private final IVelocityHandler handler;

    public VelocityHandler(IVelocityHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        this.handler.onVelocity(buf.getDouble(), buf.getDouble(), buf.getDouble());
    }
}

