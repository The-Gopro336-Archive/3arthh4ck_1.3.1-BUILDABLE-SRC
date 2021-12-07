package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;

class BlockStateManager$2
extends EventListener<WorldClientEvent.Unload> {
    BlockStateManager$2(Class target) {
        super(target);
    }

    @Override
    public void invoke(WorldClientEvent.Unload event) {
        BlockStateManager.this.callbacks.clear();
    }
}
