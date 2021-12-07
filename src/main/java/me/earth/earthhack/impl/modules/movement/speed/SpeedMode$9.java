package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$9
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!Managers.NCP.passed(100)) {
            module.vStage = 1;
            return;
        }
        if (!MovementUtil.isMoving()) {
            module.speed = MovementUtil.getSpeed(module.slow.getValue());
        }
        if (MathUtil.round(SpeedMode$9.mc.player.posY - (double)((int)SpeedMode$9.mc.player.posY), 3) == MathUtil.round(0.4, 3)) {
            SpeedMode$9.mc.player.motionY = 0.31 + MovementUtil.getJumpSpeed();
            event.setY(SpeedMode$9.mc.player.motionY);
        } else if (MathUtil.round(SpeedMode$9.mc.player.posY - (double)((int)SpeedMode$9.mc.player.posY), 3) == MathUtil.round(0.71, 3)) {
            SpeedMode$9.mc.player.motionY = 0.04 + MovementUtil.getJumpSpeed();
            event.setY(SpeedMode$9.mc.player.motionY);
        } else if (MathUtil.round(SpeedMode$9.mc.player.posY - (double)((int)SpeedMode$9.mc.player.posY), 3) == MathUtil.round(0.75, 3)) {
            SpeedMode$9.mc.player.motionY = -0.2 + MovementUtil.getJumpSpeed();
            event.setY(SpeedMode$9.mc.player.motionY);
        }
        if (SpeedMode$9.mc.world.getCollisionBoxes(null, SpeedMode$9.mc.player.getEntityBoundingBox().offset(0.0, -0.56, 0.0)).size() > 0 && MathUtil.round(SpeedMode$9.mc.player.posY - (double)((int)SpeedMode$9.mc.player.posY), 3) == MathUtil.round(0.55, 3)) {
            SpeedMode$9.mc.player.motionY = -0.14 + MovementUtil.getJumpSpeed();
            event.setY(SpeedMode$9.mc.player.motionY);
        }
        if (module.vStage != 1 || !SpeedMode$9.mc.player.collidedVertically || !MovementUtil.isMoving()) {
            if (module.vStage != 2 || !SpeedMode$9.mc.player.collidedVertically || !MovementUtil.isMoving()) {
                if (module.vStage == 3) {
                    module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                } else {
                    if (SpeedMode$9.mc.player.onGround && module.vStage > 0) {
                        module.vStage = 1.35 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01 > module.speed ? 0 : (MovementUtil.isMoving() ? 1 : 0);
                    }
                    module.speed = module.distance - module.distance / 159.0;
                }
            } else {
                SpeedMode$9.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$9.mc.player.motionY);
                module.speed *= 2.149;
            }
        } else {
            module.speed = 2.0 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
        }
        if (module.vStage > 8) {
            module.speed = MovementUtil.getSpeed(module.slow.getValue());
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        if (module.vStage > 0) {
            MovementUtil.strafe(event, module.speed);
        }
        if (MovementUtil.isMoving()) {
            ++module.vStage;
        }
    }
}
