/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;

public interface IPacketHandler {
    public void handle(IConnection var1, byte[] var2) throws IOException;
}

