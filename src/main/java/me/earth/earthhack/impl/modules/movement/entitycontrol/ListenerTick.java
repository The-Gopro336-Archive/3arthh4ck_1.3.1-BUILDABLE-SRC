/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.entitycontrol.EntityControl;

final class ListenerTick
extends ModuleListener<EntityControl, TickEvent> {
    public ListenerTick(EntityControl module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe() && ((EntityControl)this.module).jumpHeight.getValue() > 0.0) {
            ((IEntityPlayerSP)ListenerTick.mc.player).setHorseJumpPower(1.0f);
        }
    }
}

