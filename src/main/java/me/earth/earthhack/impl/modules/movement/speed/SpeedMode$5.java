package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$5
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (SpeedMode$5.mc.player.isElytraFlying()) {
            return;
        }
        if (module.LONG_JUMP.isEnabled()) {
            return;
        }
        switch (module.ncpStage) {
            case 0: {
                ++module.ncpStage;
                module.lastDist = 0.0;
                break;
            }
            case 2: {
                if (SpeedMode$5.mc.player.moveForward == 0.0f && SpeedMode$5.mc.player.moveStrafing == 0.0f || !SpeedMode$5.mc.player.onGround) break;
                SpeedMode$5.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.3999) + MovementUtil.getJumpSpeed();
                event.setY(SpeedMode$5.mc.player.motionY);
                module.speed *= 2.149;
                break;
            }
            case 3: {
                module.speed = module.lastDist - 0.7095 * (module.lastDist - MovementUtil.getSpeed(module.slow.getValue()));
                break;
            }
            default: {
                if ((SpeedMode$5.mc.world.getCollisionBoxes(null, SpeedMode$5.mc.player.getEntityBoundingBox().offset(0.0, SpeedMode$5.mc.player.motionY, 0.0)).size() > 0 || SpeedMode$5.mc.player.collidedVertically) && module.ncpStage > 0) {
                    module.ncpStage = SpeedMode$5.mc.player.moveForward == 0.0f && SpeedMode$5.mc.player.moveStrafing == 0.0f ? 0 : 1;
                }
                module.speed = module.lastDist - module.lastDist / 159.0;
            }
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
        MovementUtil.strafe(event, module.speed);
        ++module.ncpStage;
    }
}
