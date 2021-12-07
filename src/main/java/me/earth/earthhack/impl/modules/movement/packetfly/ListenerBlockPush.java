package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;

final class ListenerBlockPush
extends ModuleListener<PacketFly, BlockPushEvent> {
    public ListenerBlockPush(PacketFly module) {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event) {
        event.setCancelled(true);
    }
}
