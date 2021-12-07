package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$7
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!Managers.NCP.passed(100)) {
            module.gayStage = 1;
            return;
        }
        if (!MovementUtil.isMoving()) {
            module.speed = MovementUtil.getSpeed(module.slow.getValue());
        }
        if (module.gayStage == 1 && SpeedMode$7.mc.player.collidedVertically && MovementUtil.isMoving()) {
            module.speed = 0.25 + MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
        } else if (module.gayStage == 2 && SpeedMode$7.mc.player.collidedVertically && MovementUtil.isMoving()) {
            double yMotion;
            SpeedMode$7.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
            event.setY(yMotion);
            module.speed *= 2.149;
        } else if (module.gayStage == 3) {
            module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
        } else {
            if (SpeedMode$7.mc.player.onGround && module.gayStage > 0) {
                module.gayStage = 1.35 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01 > module.speed ? 0 : (MovementUtil.isMoving() ? 1 : 0);
            }
            module.speed = module.distance - module.distance / 159.0;
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        if (module.gayStage > 0) {
            MovementUtil.strafe(event, module.speed);
        }
        if (MovementUtil.isMoving()) {
            ++module.gayStage;
        }
    }
}
