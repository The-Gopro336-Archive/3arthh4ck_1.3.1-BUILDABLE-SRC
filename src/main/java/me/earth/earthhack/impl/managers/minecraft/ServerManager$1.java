package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;

class ServerManager$1
extends EventListener<PacketEvent.Receive<?>> {
    ServerManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event) {
        ServerManager.this.timer.reset();
    }
}
