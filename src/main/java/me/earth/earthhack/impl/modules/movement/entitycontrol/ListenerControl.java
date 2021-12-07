/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.event.events.movement.ControlEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.entitycontrol.EntityControl;

final class ListenerControl
extends ModuleListener<EntityControl, ControlEvent> {
    public ListenerControl(EntityControl module) {
        super(module, ControlEvent.class);
    }

    @Override
    public void invoke(ControlEvent event) {
        if (((EntityControl)this.module).control.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}

