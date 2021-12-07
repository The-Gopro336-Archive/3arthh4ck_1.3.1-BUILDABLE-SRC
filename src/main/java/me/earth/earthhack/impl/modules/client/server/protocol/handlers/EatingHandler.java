/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import java.io.IOException;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;

public class EatingHandler
implements IPacketHandler {
    private static final ModuleCache<AutoEat> AUTO_EAT = Caches.getModule(AutoEat.class);

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException {
        if (bytes[0] != 0) {
            AUTO_EAT.computeIfPresent(a -> a.setServer(true));
        } else {
            AUTO_EAT.computeIfPresent(a -> a.setServer(false));
        }
    }
}

