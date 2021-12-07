package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.combat.killaura.ListenerMotion;

final class ListenerRiding
extends ModuleListener<KillAura, MotionUpdateEvent.Riding> {
    public ListenerRiding(KillAura module) {
        super(module, MotionUpdateEvent.Riding.class);
    }

    @Override
    public void invoke(MotionUpdateEvent.Riding event) {
        if (event.getStage() == Stage.PRE) {
            ListenerMotion.pre((KillAura)this.module, event, ((KillAura)this.module).ridingTeleports.getValue());
        } else {
            ListenerMotion.post((KillAura)this.module);
        }
    }
}
