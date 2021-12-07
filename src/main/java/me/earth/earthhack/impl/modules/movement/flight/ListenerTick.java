/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;

final class ListenerTick
extends ModuleListener<Flight, TickEvent> {
    public ListenerTick(Flight module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe() && ((Flight)this.module).mode.getValue() == FlightMode.AAC && (float)ListenerTick.mc.player.hurtTime == 10.0f) {
            ListenerTick.mc.player.motionY = ((Flight)this.module).aacY.getValue();
        }
    }
}

