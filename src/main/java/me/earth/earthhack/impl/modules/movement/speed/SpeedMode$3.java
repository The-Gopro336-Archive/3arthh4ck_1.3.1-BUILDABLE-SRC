package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$3
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (SpeedMode$3.mc.player.onGround || module.onGroundStage == 3) {
            if (!SpeedMode$3.mc.player.collidedHorizontally && SpeedMode$3.mc.player.moveForward != 0.0f || SpeedMode$3.mc.player.moveStrafing != 0.0f) {
                if (module.onGroundStage == 2) {
                    module.speed *= 2.149;
                    module.onGroundStage = 3;
                } else if (module.onGroundStage == 3) {
                    module.onGroundStage = 2;
                    module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                } else if (PositionUtil.isBoxColliding() || SpeedMode$3.mc.player.collidedVertically) {
                    module.onGroundStage = 1;
                }
            }
            module.speed = Math.min(module.speed, module.getCap());
            module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
            MovementUtil.strafe(event, module.speed);
        }
    }
}
