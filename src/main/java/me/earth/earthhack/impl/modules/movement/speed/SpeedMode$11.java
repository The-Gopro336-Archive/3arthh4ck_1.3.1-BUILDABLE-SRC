package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$11
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!Managers.NCP.passed(100)) {
            module.constStage = 0;
            return;
        }
        if (!MovementUtil.isMoving()) {
            module.speed = MovementUtil.getSpeed(module.slow.getValue());
        }
        if (module.constStage == 0 && MovementUtil.isMoving() && SpeedMode$11.mc.player.onGround) {
            module.speed = 0.08;
        } else if (module.constStage == 1 && SpeedMode$11.mc.player.collidedVertically && MovementUtil.isMoving()) {
            module.speed = 0.25 + MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
        } else if (module.constStage == 2 && SpeedMode$11.mc.player.collidedVertically && MovementUtil.isMoving()) {
            double yMotion;
            SpeedMode$11.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
            event.setY(yMotion);
            module.speed *= module.constFactor.getValue().doubleValue();
        } else if (module.constStage == 3) {
            module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
        } else {
            if (SpeedMode$11.mc.player.onGround && module.constStage > 0) {
                module.constStage = 0;
            }
            if (!SpeedMode$11.mc.player.onGround && module.constStage > module.constOff.getValue() && module.constStage < module.constTicks.getValue()) {
                if (SpeedMode$11.mc.player.ticksExisted % 2 == 0) {
                    event.setY(0.00118212);
                } else {
                    event.setY(-0.00118212);
                }
            }
            module.speed = module.distance - module.distance / 159.0;
        }
        module.speed = Math.min(module.speed, module.getCap());
        if (module.constStage != 0) {
            module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        }
        MovementUtil.strafe(event, module.speed);
        if (MovementUtil.isMoving()) {
            ++module.constStage;
        }
    }
}
