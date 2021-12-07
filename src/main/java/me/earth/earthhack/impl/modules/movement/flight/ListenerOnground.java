/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.event.events.movement.OnGroundEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;

final class ListenerOnground
extends ModuleListener<Flight, OnGroundEvent> {
    public ListenerOnground(Flight module) {
        super(module, OnGroundEvent.class);
    }

    @Override
    public void invoke(OnGroundEvent event) {
        if (((Flight)this.module).animation.getValue().booleanValue() && ((Flight)this.module).mode.getValue() == FlightMode.Normal) {
            event.setCancelled(true);
        }
    }
}

