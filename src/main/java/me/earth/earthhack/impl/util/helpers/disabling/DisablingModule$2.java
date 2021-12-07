package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

final class DisablingModule$2
extends EventListener<DisconnectEvent> {
    final Module val$module;

    DisablingModule$2(Class target, Module module) {
        this.val$module = module;
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        Globals.mc.addScheduledTask(this.val$module::disable);
    }
}
