package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.api.UnknownProtocolException;

public interface IPacketManager {
    public void handle(IConnection var1, int var2, byte[] var3) throws UnknownProtocolException, IOException;

    public void add(int var1, IPacketHandler var2);

    public IPacketHandler getHandlerFor(int var1);
}
