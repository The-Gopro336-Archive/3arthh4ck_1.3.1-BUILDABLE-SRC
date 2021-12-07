package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$10
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!Managers.NCP.passed(100)) {
            return;
        }
        if (module.useTimer.getValue().booleanValue() && Managers.NCP.passed(250)) {
            Managers.TIMER.setTimer(1.0888f);
        }
        if (!SpeedMode$10.mc.player.collidedHorizontally) {
            if (MathUtil.round(SpeedMode$10.mc.player.posY - (double)((int)SpeedMode$10.mc.player.posY), 3) == MathUtil.round(0.4, 3)) {
                SpeedMode$10.mc.player.motionY = 0.31 + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$10.mc.player.motionY);
            } else if (MathUtil.round(SpeedMode$10.mc.player.posY - (double)((int)SpeedMode$10.mc.player.posY), 3) == MathUtil.round(0.71, 3)) {
                SpeedMode$10.mc.player.motionY = 0.04 + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$10.mc.player.motionY);
            } else if (MathUtil.round(SpeedMode$10.mc.player.posY - (double)((int)SpeedMode$10.mc.player.posY), 3) == MathUtil.round(0.75, 3)) {
                SpeedMode$10.mc.player.motionY = -0.2 - MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$10.mc.player.motionY);
            } else if (MathUtil.round(SpeedMode$10.mc.player.posY - (double)((int)SpeedMode$10.mc.player.posY), 3) == MathUtil.round(0.55, 3)) {
                SpeedMode$10.mc.player.motionY = -0.14 + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$10.mc.player.motionY);
            } else if (MathUtil.round(SpeedMode$10.mc.player.posY - (double)((int)SpeedMode$10.mc.player.posY), 3) == MathUtil.round(0.41, 3)) {
                SpeedMode$10.mc.player.motionY = -0.2 + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$10.mc.player.motionY);
            }
        }
        if (module.lowStage == 1 && MovementUtil.isMoving()) {
            module.speed = 1.35 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
        } else if (module.lowStage == 2 && MovementUtil.isMoving()) {
            SpeedMode$10.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.3999) + MovementUtil.getJumpSpeed();
            event.setY(SpeedMode$10.mc.player.motionY);
            module.speed *= module.boost ? 1.5685 : 1.3445;
        } else if (module.lowStage == 3) {
            module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
            module.boost = !module.boost;
        } else {
            if (SpeedMode$10.mc.player.onGround && module.lowStage > 0) {
                module.lowStage = MovementUtil.isMoving() ? 1 : 0;
            }
            module.speed = module.distance - module.distance / 159.0;
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        MovementUtil.strafe(event, module.speed);
        if (MovementUtil.isMoving()) {
            ++module.lowStage;
        }
    }
}
