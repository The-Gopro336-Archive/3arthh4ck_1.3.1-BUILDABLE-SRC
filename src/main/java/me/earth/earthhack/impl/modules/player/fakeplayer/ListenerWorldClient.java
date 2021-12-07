package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;

final class ListenerWorldClient
extends ModuleListener<FakePlayer, WorldClientEvent.Load> {
    public ListenerWorldClient(FakePlayer module) {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        ((FakePlayer)this.module).positions.clear();
    }
}
