package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.util.helpers.disabling.IDisablingModule;

final class DisablingModule$1
extends EventListener<DisconnectEvent> {
    final IDisablingModule val$disabling;

    DisablingModule$1(Class target, IDisablingModule iDisablingModule) {
        this.val$disabling = iDisablingModule;
        super(target);
    }

    @Override
    public void invoke(DisconnectEvent event) {
        Globals.mc.addScheduledTask(this.val$disabling::onDisconnect);
    }
}
