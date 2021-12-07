package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;

final class ListenerGameLoop
extends ModuleListener<AutoArmor, GameLoopEvent> {
    public ListenerGameLoop(AutoArmor module) {
        super(module, GameLoopEvent.class, -5);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        ((AutoArmor)this.module).runClick();
    }
}
