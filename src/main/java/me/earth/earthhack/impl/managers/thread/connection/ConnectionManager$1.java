package me.earth.earthhack.impl.managers.thread.connection;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketPlayerListItem;

class ConnectionManager$1
extends EventListener<PacketEvent.Receive<SPacketPlayerListItem>> {
    ConnectionManager$1(Class target, Class type) {
        super(target, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerListItem> event) {
        ConnectionManager.this.onEvent(event);
    }
}
