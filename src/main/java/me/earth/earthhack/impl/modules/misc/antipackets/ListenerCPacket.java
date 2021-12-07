package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPackets;

final class ListenerCPacket
extends ModuleListener<AntiPackets, PacketEvent.Send<?>> {
    public ListenerCPacket(AntiPackets module) {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event) {
        ((AntiPackets)this.module).onPacket(event, false);
    }
}
