package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMove
extends ModuleListener<Phase, MoveEvent> {
    public ListenerMove(Phase module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((Phase)this.module).mode.getValue() == PhaseMode.Constantiam && ((Phase)this.module).isPhasing() && ((Phase)this.module).constStrafe.getValue().booleanValue()) {
            MovementUtil.strafe(event, 0.2873 * ((Phase)this.module).constSpeed.getValue());
        }
        if (((Phase)this.module).mode.getValue() == PhaseMode.ConstantiamNew && ((Phase)this.module).isPhasing()) {
            if (ListenerMove.mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(ListenerMove.mc.player.motionY += (double)0.09f);
            } else if (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.setY(ListenerMove.mc.player.motionY -= 0.0);
            } else {
                ListenerMove.mc.player.motionY = 0.0;
                event.setY(0.0);
            }
            MovementUtil.strafe(event, 0.2783);
        }
    }
}
