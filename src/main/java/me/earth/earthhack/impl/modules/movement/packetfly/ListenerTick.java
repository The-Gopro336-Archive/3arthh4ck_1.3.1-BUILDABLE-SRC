/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import java.util.concurrent.TimeUnit;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;

final class ListenerTick
extends ModuleListener<PacketFly, ListenerTick> {
    public ListenerTick(PacketFly module) {
        super(module, ListenerTick.class);
    }

    @Override
    public void invoke(ListenerTick event) {
        ((PacketFly)this.module).posLooks.entrySet().removeIf(entry -> System.currentTimeMillis() - ((TimeVec)((Object)((Object)entry.getValue()))).getTime() > TimeUnit.SECONDS.toMillis(30L));
    }
}

