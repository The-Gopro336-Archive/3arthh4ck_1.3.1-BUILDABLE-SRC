package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;

final class ListenerRender
extends ModuleListener<Phase, Render3DEvent> {
    public ListenerRender(Phase module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (((Phase)this.module).esp.getValue().booleanValue() && ((Phase)this.module).pos != null && !((Phase)this.module).clickTimer.passed(750L)) {
            ((Phase)this.module).renderPos(((Phase)this.module).pos);
        }
    }
}
