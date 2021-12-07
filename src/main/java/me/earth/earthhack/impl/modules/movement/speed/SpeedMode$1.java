package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$1
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (SpeedMode$1.mc.player.isElytraFlying()) {
            return;
        }
        if (module.LONG_JUMP.isEnabled()) {
            return;
        }
        if (!module.noWaterInstant.getValue().booleanValue() || !SpeedMode$1.mc.player.isInWater() && !SpeedMode$1.mc.player.isInLava()) {
            MovementUtil.strafe(event, MovementUtil.getSpeed(module.slow.getValue()));
        }
    }
}
