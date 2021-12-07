package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.management.Management;

final class ListenerGameLoop
extends ModuleListener<Management, GameLoopEvent> {
    public ListenerGameLoop(Management module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (ListenerGameLoop.mc.world != null && ((Management)this.module).time.getValue() != 0) {
            ListenerGameLoop.mc.world.setWorldTime(-((Management)this.module).time.getValue().intValue());
        }
    }
}
