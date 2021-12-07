/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;

final class ListenerTick
extends ModuleListener<AutoCrystal, TickEvent> {
    public ListenerTick(AutoCrystal module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            ((AutoCrystal)this.module).checkExecutor();
            ((AutoCrystal)this.module).placed.values().removeIf(stamp -> System.currentTimeMillis() - stamp.getTimeStamp() > (long)((AutoCrystal)this.module).removeTime.getValue().intValue());
            ((AutoCrystal)this.module).crystalRender.tick();
            if (!((AutoCrystal)this.module).idHelper.isUpdated()) {
                ((AutoCrystal)this.module).idHelper.update();
                ((AutoCrystal)this.module).idHelper.setUpdated(true);
            }
            ((AutoCrystal)this.module).weaknessHelper.updateWeakness();
        }
    }
}

