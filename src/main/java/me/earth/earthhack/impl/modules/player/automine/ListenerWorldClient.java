package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;

final class ListenerWorldClient
extends ModuleListener<AutoMine, WorldClientEvent.Load> {
    public ListenerWorldClient(AutoMine module) {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        ((AutoMine)this.module).reset(true);
        ((AutoMine)this.module).blackList.clear();
    }
}
