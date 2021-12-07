/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

public class MessageHandler
implements IPacketHandler {
    private final Function<String, String> format;
    private final ILogger logger;

    public MessageHandler(ILogger logger) {
        this(logger, null);
    }

    public MessageHandler(ILogger logger, Function<String, String> format) {
        this.logger = logger;
        this.format = format;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        String message = new String(bytes, StandardCharsets.UTF_8);
        if (this.format != null) {
            this.logger.log(this.format.apply(message));
        } else {
            this.logger.log(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}

