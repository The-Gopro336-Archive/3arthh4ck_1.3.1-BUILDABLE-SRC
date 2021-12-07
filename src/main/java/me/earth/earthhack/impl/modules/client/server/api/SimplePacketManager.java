package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.api.IPacketManager;
import me.earth.earthhack.impl.modules.client.server.api.UnknownProtocolException;

public class SimplePacketManager
implements IPacketManager {
    private final Map<Integer, IPacketHandler> handlers = new ConcurrentHashMap<Integer, IPacketHandler>();

    @Override
    public void handle(IConnection connection, int id, byte[] bytes) throws UnknownProtocolException, IOException {
        IPacketHandler handler = this.handlers.get(id);
        if (handler == null) {
            throw new UnknownProtocolException(id);
        }
        handler.handle(connection, bytes);
    }

    @Override
    public void add(int id, IPacketHandler handler) {
        this.handlers.put(id, handler);
    }

    @Override
    public IPacketHandler getHandlerFor(int id) {
        return this.handlers.get(id);
    }
}
