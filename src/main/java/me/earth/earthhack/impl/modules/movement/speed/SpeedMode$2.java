package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;

final class SpeedMode$2
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        OnGround.move(event, module);
    }
}
