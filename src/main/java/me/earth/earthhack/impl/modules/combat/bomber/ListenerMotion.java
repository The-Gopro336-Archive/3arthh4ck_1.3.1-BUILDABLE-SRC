/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.bomber;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bomber.CrystalBomber;

final class ListenerMotion
extends ModuleListener<CrystalBomber, MotionUpdateEvent> {
    public ListenerMotion(CrystalBomber module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        ((CrystalBomber)this.module).doCrystalBomber(event);
    }
}

