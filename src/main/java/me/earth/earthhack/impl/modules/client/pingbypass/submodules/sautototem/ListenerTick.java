/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;

final class ListenerTick
extends ModuleListener<ServerAutoTotem, TickEvent> {
    public ListenerTick(ServerAutoTotem module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        ((ServerAutoTotem)this.module).onTick();
    }
}

