package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;

final class ListenerLiquidJump
extends ModuleListener<Jesus, LiquidJumpEvent> {
    public ListenerLiquidJump(Jesus module) {
        super(module, LiquidJumpEvent.class);
    }

    @Override
    public void invoke(LiquidJumpEvent event) {
        if (ListenerLiquidJump.mc.player != null && ListenerLiquidJump.mc.player.equals(event.getEntity()) && (ListenerLiquidJump.mc.player.isInWater() || ListenerLiquidJump.mc.player.isInLava()) && (ListenerLiquidJump.mc.player.motionY == 0.1 || ListenerLiquidJump.mc.player.motionY == 0.5)) {
            event.setCancelled(true);
        }
    }
}
