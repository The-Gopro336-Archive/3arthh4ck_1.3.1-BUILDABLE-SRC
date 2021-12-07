package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.util.helpers.disabling.IDisablingModule;

final class DisablingModule$5
extends EventListener<ShutDownEvent> {
    final IDisablingModule val$disabling;

    DisablingModule$5(Class target, IDisablingModule iDisablingModule) {
        this.val$disabling = iDisablingModule;
        super(target);
    }

    @Override
    public void invoke(ShutDownEvent event) {
        Globals.mc.addScheduledTask(this.val$disabling::onDisconnect);
    }
}
