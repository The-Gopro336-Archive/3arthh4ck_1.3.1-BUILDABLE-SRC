package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;

final class ListenerDeath
extends ModuleListener<Packets, DeathEvent> {
    public ListenerDeath(Packets module) {
        super(module, DeathEvent.class, Integer.MAX_VALUE);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (((Packets)this.module).fastDeath.getValue().booleanValue() && !event.getEntity().equals(ListenerDeath.mc.player)) {
            Managers.SET_DEAD.setDead(event.getEntity());
        }
    }
}
