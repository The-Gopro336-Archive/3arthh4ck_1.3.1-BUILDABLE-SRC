package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;

final class ListenerTick
extends ModuleListener<AutoSprint, TickEvent> {
    public ListenerTick(AutoSprint module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (AutoSprint.canSprint() && ((AutoSprint)this.module).mode.getValue() == SprintMode.Legit || AutoSprint.canSprintBetter() && ((AutoSprint)this.module).mode.getValue() == SprintMode.Rage) {
            ((AutoSprint)this.module).mode.getValue().sprint();
        }
    }
}
