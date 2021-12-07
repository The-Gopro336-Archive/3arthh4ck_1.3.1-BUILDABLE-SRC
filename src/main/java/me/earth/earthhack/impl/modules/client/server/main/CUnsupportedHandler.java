/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main;

import java.io.IOException;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

public class CUnsupportedHandler
implements IPacketHandler {
    private final ILogger logger;
    private final int id;

    public CUnsupportedHandler(ILogger logger, int id) {
        this.logger = logger;
        this.id = id;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        this.logger.log("Received packet with unsupported id: " + this.id);
    }
}

