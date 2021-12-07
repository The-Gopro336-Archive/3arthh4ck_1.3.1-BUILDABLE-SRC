/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

public class GlobalMessageHandler
implements IPacketHandler {
    private final IConnectionManager manager;
    private final ILogger logger;

    public GlobalMessageHandler(ILogger logger, IConnectionManager manager) {
        this.logger = logger;
        this.manager = manager;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        String message = new String(bytes, StandardCharsets.UTF_8);
        this.logger.log(message);
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 8);
        buffer.putInt(2);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        byte[] globalMessage = buffer.array();
        for (IConnection conn : this.manager.getConnections()) {
            if (conn == null || conn.equals(connection)) continue;
            conn.send(globalMessage);
        }
    }
}

