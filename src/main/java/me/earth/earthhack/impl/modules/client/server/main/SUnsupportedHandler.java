/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main;

import java.io.IOException;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;

public class SUnsupportedHandler
implements IPacketHandler {
    private final String message;

    public SUnsupportedHandler(String message) {
        this.message = message;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        ProtocolUtil.sendMessage(connection, 2, this.message);
    }
}

