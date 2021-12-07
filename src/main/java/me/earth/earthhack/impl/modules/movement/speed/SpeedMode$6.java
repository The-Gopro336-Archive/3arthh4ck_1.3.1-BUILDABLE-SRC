package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class SpeedMode$6
extends SpeedMode {
    @Override
    public void move(MoveEvent event, Speed module) {
        if (!MovementUtil.isMoving()) {
            return;
        }
        if (SpeedMode$6.mc.player.isElytraFlying()) {
            return;
        }
        if (module.LONG_JUMP.isEnabled()) {
            return;
        }
        if (!Managers.NCP.passed(module.lagTime.getValue())) {
            return;
        }
        if (module.useTimer.getValue().booleanValue() && Managers.NCP.passed(250)) {
            Managers.TIMER.setTimer(1.0888f);
        }
        if (module.stage == 1 && MovementUtil.isMoving()) {
            module.speed = 1.35 * MovementUtil.getSpeed(module.slow.getValue(), module.strafeSpeed.getValue()) - 0.01;
        } else if (module.stage == 2 && MovementUtil.isMoving()) {
            double yMotion;
            SpeedMode$6.mc.player.motionY = yMotion = 0.3999 + MovementUtil.getJumpSpeed();
            event.setY(yMotion);
            module.speed *= module.boost ? 1.6835 : 1.395;
        } else if (module.stage == 3) {
            module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue(), module.strafeSpeed.getValue()));
            module.boost = !module.boost;
        } else {
            if ((SpeedMode$6.mc.world.getCollisionBoxes(null, SpeedMode$6.mc.player.getEntityBoundingBox().offset(0.0, SpeedMode$6.mc.player.motionY, 0.0)).size() > 0 || SpeedMode$6.mc.player.collidedVertically) && module.stage > 0) {
                module.stage = MovementUtil.isMoving() ? 1 : 0;
            }
            module.speed = module.distance - module.distance / 159.0;
        }
        module.speed = Math.min(module.speed, module.getCap());
        module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue(), module.strafeSpeed.getValue()));
        MovementUtil.strafe(event, module.speed);
        if (MovementUtil.isMoving()) {
            ++module.stage;
        }
    }
}
