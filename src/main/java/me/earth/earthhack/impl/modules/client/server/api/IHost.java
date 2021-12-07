/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;

public interface IHost
extends ICloseable {
    public IConnectionManager getConnectionManager();

    public int getPort();
}

