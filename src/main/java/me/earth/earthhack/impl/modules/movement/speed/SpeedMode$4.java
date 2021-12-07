package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$4
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        MovementUtil.strafe(event, module.speedSet.getValue() / 10.0);
    }
}
