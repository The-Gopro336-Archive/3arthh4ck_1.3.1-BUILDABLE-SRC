package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class BlockStateManager$1
extends EventListener<WorldClientEvent.Load> {
    BlockStateManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        BlockStateManager.this.callbacks.clear();
    }
}
