package me.earth.earthhack.impl.modules.client.server.api;

import java.util.List;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionListener;
import me.earth.earthhack.impl.modules.client.server.api.IPacketManager;
import me.earth.earthhack.impl.modules.client.server.api.ISender;

public interface IConnectionManager
extends ISender {
    public IPacketManager getHandler();

    public boolean accept(IConnection var1);

    public void remove(IConnection var1);

    public List<IConnection> getConnections();

    public void addListener(IConnectionListener var1);

    public void removeListener(IConnectionListener var1);
}
