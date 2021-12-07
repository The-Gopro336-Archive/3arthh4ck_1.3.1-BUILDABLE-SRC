package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;

final class DisablingModule$6
extends EventListener<ShutDownEvent> {
    final Module val$module;

    DisablingModule$6(Class target, Module module) {
        this.val$module = module;
        super(target);
    }

    @Override
    public void invoke(ShutDownEvent event) {
        Globals.mc.addScheduledTask(this.val$module::disable);
    }
}
