package me.earth.earthhack.impl.modules.combat.snowballer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.snowballer.Snowballer;

final class ListenerMotion
extends ModuleListener<Snowballer, MotionUpdateEvent> {
    public ListenerMotion(Snowballer module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            ((Snowballer)this.module).runPre(event);
        } else if (event.getStage() == Stage.POST) {
            ((Snowballer)this.module).runPost(event);
        }
    }
}
