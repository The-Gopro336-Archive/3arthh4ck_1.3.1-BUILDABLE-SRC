package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.movement.SprintEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;

final class ListenerSprint
extends ModuleListener<NoSlowDown, SprintEvent> {
    public ListenerSprint(NoSlowDown module) {
        super(module, SprintEvent.class);
    }

    @Override
    public void invoke(SprintEvent event) {
        if (((NoSlowDown)this.module).sprint.getValue().booleanValue() && ((NoSlowDown)this.module).items.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}
