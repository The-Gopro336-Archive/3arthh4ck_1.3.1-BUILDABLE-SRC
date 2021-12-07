package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;

final class ListenerInput
extends ModuleListener<Phase, MovementInputEvent> {
    public ListenerInput(Phase module) {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (((Phase)this.module).autoSneak.getValue().booleanValue()) {
            event.getInput().sneak = ((Phase)this.module).requireForward.getValue() == false || ListenerInput.mc.gameSettings.keyBindForward.isKeyDown();
        }
    }
}
