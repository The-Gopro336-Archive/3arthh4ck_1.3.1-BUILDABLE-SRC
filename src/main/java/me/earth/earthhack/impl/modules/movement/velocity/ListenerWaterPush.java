/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.movement.WaterPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;

final class ListenerWaterPush
extends ModuleListener<Velocity, WaterPushEvent> {
    public ListenerWaterPush(Velocity module) {
        super(module, WaterPushEvent.class);
    }

    @Override
    public void invoke(WaterPushEvent event) {
        if (((Velocity)this.module).water.getValue().booleanValue() && event.getEntity().equals((Object)ListenerWaterPush.mc.player)) {
            event.setCancelled(true);
        }
    }
}

