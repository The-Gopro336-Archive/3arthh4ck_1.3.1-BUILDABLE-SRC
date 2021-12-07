package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.impl.modules.client.server.api.IConnectionEntry;

public interface IServerList {
    public IConnectionEntry[] get();

    public void set(IConnectionEntry[] var1);
}
