package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

public enum SpeedMode implements Globals
{
    Instant{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (1.mc.player.isElytraFlying()) {
                return;
            }
            if (module.LONG_JUMP.isEnabled()) {
                return;
            }
            if (!module.noWaterInstant.getValue().booleanValue() || !1.mc.player.isInWater() && !1.mc.player.isInLava()) {
                MovementUtil.strafe(event, MovementUtil.getSpeed(module.slow.getValue()));
            }
        }
    }
    ,
    OldGround{

        @Override
        public void move(MoveEvent event, Speed module) {
            OnGround.move(event, module);
        }
    }
    ,
    OnGround{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (3.mc.player.onGround || module.onGroundStage == 3) {
                if (!3.mc.player.collidedHorizontally && 3.mc.player.moveForward != 0.0f || 3.mc.player.moveStrafing != 0.0f) {
                    if (module.onGroundStage == 2) {
                        module.speed *= 2.149;
                        module.onGroundStage = 3;
                    } else if (module.onGroundStage == 3) {
                        module.onGroundStage = 2;
                        module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                    } else if (PositionUtil.isBoxColliding() || 3.mc.player.collidedVertically) {
                        module.onGroundStage = 1;
                    }
                }
                module.speed = Math.min(module.speed, module.getCap());
                module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
                MovementUtil.strafe(event, module.speed);
            }
        }
    }
    ,
    Vanilla{

        @Override
        public void move(MoveEvent event, Speed module) {
            MovementUtil.strafe(event, module.speedSet.getValue() / 10.0);
        }
    }
    ,
    NCP{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (5.mc.player.isElytraFlying()) {
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
                    if (5.mc.player.moveForward == 0.0f && 5.mc.player.moveStrafing == 0.0f || !5.mc.player.onGround) break;
                    5.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.3999) + MovementUtil.getJumpSpeed();
                    event.setY(5.mc.player.motionY);
                    module.speed *= 2.149;
                    break;
                }
                case 3: {
                    module.speed = module.lastDist - 0.7095 * (module.lastDist - MovementUtil.getSpeed(module.slow.getValue()));
                    break;
                }
                default: {
                    if ((5.mc.world.getCollisionBoxes(null, 5.mc.player.getEntityBoundingBox().offset(0.0, 5.mc.player.motionY, 0.0)).size() > 0 || 5.mc.player.collidedVertically) && module.ncpStage > 0) {
                        module.ncpStage = 5.mc.player.moveForward == 0.0f && 5.mc.player.moveStrafing == 0.0f ? 0 : 1;
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
    ,
    Strafe{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!MovementUtil.isMoving()) {
                return;
            }
            if (6.mc.player.isElytraFlying()) {
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
                6.mc.player.motionY = yMotion = 0.3999 + MovementUtil.getJumpSpeed();
                event.setY(yMotion);
                module.speed *= module.boost ? 1.6835 : 1.395;
            } else if (module.stage == 3) {
                module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue(), module.strafeSpeed.getValue()));
                module.boost = !module.boost;
            } else {
                if ((6.mc.world.getCollisionBoxes(null, 6.mc.player.getEntityBoundingBox().offset(0.0, 6.mc.player.motionY, 0.0)).size() > 0 || 6.mc.player.collidedVertically) && module.stage > 0) {
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
    ,
    GayHop{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!Managers.NCP.passed(100)) {
                module.gayStage = 1;
                return;
            }
            if (!MovementUtil.isMoving()) {
                module.speed = MovementUtil.getSpeed(module.slow.getValue());
            }
            if (module.gayStage == 1 && 7.mc.player.collidedVertically && MovementUtil.isMoving()) {
                module.speed = 0.25 + MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
            } else if (module.gayStage == 2 && 7.mc.player.collidedVertically && MovementUtil.isMoving()) {
                double yMotion;
                7.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
                event.setY(yMotion);
                module.speed *= 2.149;
            } else if (module.gayStage == 3) {
                module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
            } else {
                if (7.mc.player.onGround && module.gayStage > 0) {
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
    ,
    Bhop{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!Managers.NCP.passed(100)) {
                module.bhopStage = 4;
                return;
            }
            if (MathUtil.round(8.mc.player.posY - (double)((int)8.mc.player.posY), 3) == MathUtil.round(0.138, 3)) {
                8.mc.player.motionY -= 0.08 + MovementUtil.getJumpSpeed();
                event.setY(event.getY() - (0.0931 + MovementUtil.getJumpSpeed()));
                8.mc.player.posY -= 0.0931 + MovementUtil.getJumpSpeed();
            }
            if ((double)module.bhopStage != 2.0 || !MovementUtil.isMoving()) {
                if ((double)module.bhopStage == 3.0) {
                    module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                } else {
                    if (8.mc.player.onGround) {
                        module.bhopStage = 1;
                    }
                    module.speed = module.distance - module.distance / 159.0;
                }
            } else {
                double yMotion;
                8.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
                event.setY(yMotion);
                module.speed *= 2.149;
            }
            module.speed = Math.min(module.speed, module.getCap());
            module.speed = Math.max(module.speed, MovementUtil.getSpeed(module.slow.getValue()));
            MovementUtil.strafe(event, module.speed);
            ++module.bhopStage;
        }
    }
    ,
    VHop{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!Managers.NCP.passed(100)) {
                module.vStage = 1;
                return;
            }
            if (!MovementUtil.isMoving()) {
                module.speed = MovementUtil.getSpeed(module.slow.getValue());
            }
            if (MathUtil.round(9.mc.player.posY - (double)((int)9.mc.player.posY), 3) == MathUtil.round(0.4, 3)) {
                9.mc.player.motionY = 0.31 + MovementUtil.getJumpSpeed();
                event.setY(9.mc.player.motionY);
            } else if (MathUtil.round(9.mc.player.posY - (double)((int)9.mc.player.posY), 3) == MathUtil.round(0.71, 3)) {
                9.mc.player.motionY = 0.04 + MovementUtil.getJumpSpeed();
                event.setY(9.mc.player.motionY);
            } else if (MathUtil.round(9.mc.player.posY - (double)((int)9.mc.player.posY), 3) == MathUtil.round(0.75, 3)) {
                9.mc.player.motionY = -0.2 + MovementUtil.getJumpSpeed();
                event.setY(9.mc.player.motionY);
            }
            if (9.mc.world.getCollisionBoxes(null, 9.mc.player.getEntityBoundingBox().offset(0.0, -0.56, 0.0)).size() > 0 && MathUtil.round(9.mc.player.posY - (double)((int)9.mc.player.posY), 3) == MathUtil.round(0.55, 3)) {
                9.mc.player.motionY = -0.14 + MovementUtil.getJumpSpeed();
                event.setY(9.mc.player.motionY);
            }
            if (module.vStage != 1 || !9.mc.player.collidedVertically || !MovementUtil.isMoving()) {
                if (module.vStage != 2 || !9.mc.player.collidedVertically || !MovementUtil.isMoving()) {
                    if (module.vStage == 3) {
                        module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                    } else {
                        if (9.mc.player.onGround && module.vStage > 0) {
                            module.vStage = 1.35 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01 > module.speed ? 0 : (MovementUtil.isMoving() ? 1 : 0);
                        }
                        module.speed = module.distance - module.distance / 159.0;
                    }
                } else {
                    9.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
                    event.setY(9.mc.player.motionY);
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
    ,
    LowHop{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!Managers.NCP.passed(100)) {
                return;
            }
            if (module.useTimer.getValue().booleanValue() && Managers.NCP.passed(250)) {
                Managers.TIMER.setTimer(1.0888f);
            }
            if (!10.mc.player.collidedHorizontally) {
                if (MathUtil.round(10.mc.player.posY - (double)((int)10.mc.player.posY), 3) == MathUtil.round(0.4, 3)) {
                    10.mc.player.motionY = 0.31 + MovementUtil.getJumpSpeed();
                    event.setY(10.mc.player.motionY);
                } else if (MathUtil.round(10.mc.player.posY - (double)((int)10.mc.player.posY), 3) == MathUtil.round(0.71, 3)) {
                    10.mc.player.motionY = 0.04 + MovementUtil.getJumpSpeed();
                    event.setY(10.mc.player.motionY);
                } else if (MathUtil.round(10.mc.player.posY - (double)((int)10.mc.player.posY), 3) == MathUtil.round(0.75, 3)) {
                    10.mc.player.motionY = -0.2 - MovementUtil.getJumpSpeed();
                    event.setY(10.mc.player.motionY);
                } else if (MathUtil.round(10.mc.player.posY - (double)((int)10.mc.player.posY), 3) == MathUtil.round(0.55, 3)) {
                    10.mc.player.motionY = -0.14 + MovementUtil.getJumpSpeed();
                    event.setY(10.mc.player.motionY);
                } else if (MathUtil.round(10.mc.player.posY - (double)((int)10.mc.player.posY), 3) == MathUtil.round(0.41, 3)) {
                    10.mc.player.motionY = -0.2 + MovementUtil.getJumpSpeed();
                    event.setY(10.mc.player.motionY);
                }
            }
            if (module.lowStage == 1 && MovementUtil.isMoving()) {
                module.speed = 1.35 * MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
            } else if (module.lowStage == 2 && MovementUtil.isMoving()) {
                10.mc.player.motionY = (PositionUtil.isBoxColliding() ? 0.2 : 0.3999) + MovementUtil.getJumpSpeed();
                event.setY(10.mc.player.motionY);
                module.speed *= module.boost ? 1.5685 : 1.3445;
            } else if (module.lowStage == 3) {
                module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
                module.boost = !module.boost;
            } else {
                if (10.mc.player.onGround && module.lowStage > 0) {
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
    ,
    Constantiam{

        @Override
        public void move(MoveEvent event, Speed module) {
            if (!Managers.NCP.passed(100)) {
                module.constStage = 0;
                return;
            }
            if (!MovementUtil.isMoving()) {
                module.speed = MovementUtil.getSpeed(module.slow.getValue());
            }
            if (module.constStage == 0 && MovementUtil.isMoving() && 11.mc.player.onGround) {
                module.speed = 0.08;
            } else if (module.constStage == 1 && 11.mc.player.collidedVertically && MovementUtil.isMoving()) {
                module.speed = 0.25 + MovementUtil.getSpeed(module.slow.getValue()) - 0.01;
            } else if (module.constStage == 2 && 11.mc.player.collidedVertically && MovementUtil.isMoving()) {
                double yMotion;
                11.mc.player.motionY = yMotion = (PositionUtil.isBoxColliding() ? 0.2 : 0.4) + MovementUtil.getJumpSpeed();
                event.setY(yMotion);
                module.speed *= module.constFactor.getValue().doubleValue();
            } else if (module.constStage == 3) {
                module.speed = module.distance - 0.66 * (module.distance - MovementUtil.getSpeed(module.slow.getValue()));
            } else {
                if (11.mc.player.onGround && module.constStage > 0) {
                    module.constStage = 0;
                }
                if (!11.mc.player.onGround && module.constStage > module.constOff.getValue() && module.constStage < module.constTicks.getValue()) {
                    if (11.mc.player.ticksExisted % 2 == 0) {
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
    };


    public abstract void move(MoveEvent var1, Speed var2);
}
