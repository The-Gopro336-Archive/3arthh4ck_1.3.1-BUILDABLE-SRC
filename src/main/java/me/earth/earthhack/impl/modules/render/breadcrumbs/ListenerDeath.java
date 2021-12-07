package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;

final class ListenerDeath
extends ModuleListener<BreadCrumbs, DeathEvent> {
    public ListenerDeath(BreadCrumbs module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (((BreadCrumbs)this.module).clearD.getValue().booleanValue() && event.getEntity() != null && event.getEntity().equals(ListenerDeath.mc.player)) {
            mc.addScheduledTask(((BreadCrumbs)this.module).positions::clear);
        }
    }
}
