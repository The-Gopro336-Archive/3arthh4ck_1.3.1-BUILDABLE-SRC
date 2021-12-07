package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$8
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!Managers.NCP.passed(100)) {
            module.bhopStage = 4;
            return;
        }
        if (MathUtil.round(SpeedMode$8.mc.player.posY - (double)((int)SpeedMode$8.mc.player.posY), 3) == MathUtil.round(0.138, 3)) {
            SpeedMode$8.mc.player.motionY -= 0.08 + MovementUtil.getJumpSpeed();
            event.setY(event.getY() - (0.0931 + MovementUtil.getJumpSpeed()));
            SpeedMode$8.mc.player.posY -= 0.0931 + MovementUtil.getJumpSpeed();
        }
        if ((double)module.bhopStage != 2.0 || !MovementUtil.isMoving()) {
            if ((double)module.bhopStage == 3.0) {
                module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
            } else {
                if (SpeedMode$8.mc.player.onGround) {
                    module.bhopStage = 1;
                }
                module.speed = module.distance - module.distance / 159.0;
            }
        } else {
            double yMotion;
            SpeedMode$8.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
            event.setY(yMotion);
            module.speed *= 2.149;
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        MovementUtil.strafe(event, module.speed);
        ++module.bhopStage;
    }
}
