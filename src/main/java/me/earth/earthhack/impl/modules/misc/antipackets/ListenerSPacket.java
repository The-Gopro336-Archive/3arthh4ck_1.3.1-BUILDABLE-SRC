/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPackets;

final class ListenerSPacket
extends ModuleListener<AntiPackets, PacketEvent.Receive<?>> {
    public ListenerSPacket(AntiPackets module) {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event) {
        ((AntiPackets)this.module).onPacket(event, true);
    }
}

