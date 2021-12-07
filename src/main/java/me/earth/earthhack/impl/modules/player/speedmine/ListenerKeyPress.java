package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;

final class ListenerKeyPress
extends ModuleListener<Speedmine, KeyboardEvent> {
    public ListenerKeyPress(Speedmine module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getEventState() && event.getKey() == ((Bind)((Speedmine)this.module).breakBind.getValue()).getKey()) {
            ((Speedmine)this.module).tryBreak();
        }
    }
}
