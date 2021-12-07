package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.replenish.Replenish;

final class ListenerLogout
extends ModuleListener<Replenish, DisconnectEvent> {
    public ListenerLogout(Replenish module) {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        ((Replenish)this.module).clear();
    }
}
