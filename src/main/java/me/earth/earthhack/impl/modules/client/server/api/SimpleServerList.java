/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.impl.modules.client.server.api.IConnectionEntry;
import me.earth.earthhack.impl.modules.client.server.api.IServerList;

public class SimpleServerList
implements IServerList {
    private IConnectionEntry[] entries = new IConnectionEntry[0];

    @Override
    public IConnectionEntry[] get() {
        return this.entries;
    }

    @Override
    public void set(IConnectionEntry[] entries) {
        this.entries = entries;
    }
}

