package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.util.helpers.disabling.IDisablingModule;

final class DisablingModule$3
extends EventListener<DeathEvent> {
    final IDisablingModule val$disabling;

    DisablingModule$3(Class target, IDisablingModule iDisablingModule) {
        this.val$disabling = iDisablingModule;
        super(target);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (event.getEntity() != null && event.getEntity().equals(Globals.mc.player)) {
            Globals.mc.addScheduledTask(this.val$disabling::onDeath);
        }
    }
}
