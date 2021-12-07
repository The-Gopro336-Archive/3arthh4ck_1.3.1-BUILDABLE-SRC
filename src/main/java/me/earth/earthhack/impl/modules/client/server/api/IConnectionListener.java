/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;

public interface IConnectionListener {
    public void onJoin(IConnectionManager var1, IConnection var2);

    public void onLeave(IConnectionManager var1, IConnection var2);
}

