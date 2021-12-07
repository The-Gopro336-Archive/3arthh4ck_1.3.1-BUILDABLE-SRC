package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;

final class DisablingModule$4
extends EventListener<DeathEvent> {
    final Module val$module;

    DisablingModule$4(Class target, Module module) {
        this.val$module = module;
        super(target);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (event.getEntity() != null && event.getEntity().equals(Globals.mc.player)) {
            Globals.mc.addScheduledTask(this.val$module::disable);
        }
    }
}
