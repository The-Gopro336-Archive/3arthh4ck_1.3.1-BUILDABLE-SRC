package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.speed.Speed;

final class ListenerBlockPush
extends ModuleListener<Speed, BlockPushEvent> {
    public ListenerBlockPush(Speed module) {
        super(module, BlockPushEvent.class, 999);
    }

    @Override
    public void invoke(BlockPushEvent event) {
        if (((Speed)this.module).lagOut.getValue().booleanValue()) {
            event.setCancelled(false);
        }
    }
}
