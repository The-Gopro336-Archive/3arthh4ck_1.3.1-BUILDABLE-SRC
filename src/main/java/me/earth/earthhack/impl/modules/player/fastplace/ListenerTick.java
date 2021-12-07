package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;

final class ListenerTick
extends ModuleListener<FastPlace, TickEvent> {
    public ListenerTick(FastPlace module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (ListenerTick.mc.player != null) {
            ((FastPlace)this.module).onTick();
        }
    }
}
