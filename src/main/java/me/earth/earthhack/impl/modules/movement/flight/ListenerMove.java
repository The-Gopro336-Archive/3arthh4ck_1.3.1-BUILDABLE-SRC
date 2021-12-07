/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.MobEffects
 */
package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;

final class ListenerMove
extends ModuleListener<Flight, MoveEvent> {
    public ListenerMove(Flight module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        float forward = ListenerMove.mc.player.movementInput.moveForward;
        float strafe = ListenerMove.mc.player.movementInput.moveStrafe;
        switch (((Flight)this.module).mode.getValue()) {
            case ConstantiamNew: {
                if (!MovementUtil.isMoving()) break;
                switch (((Flight)this.module).constNewStage) {
                    case 0: {
                        if (!ListenerMove.mc.player.onGround || !ListenerMove.mc.player.collidedVertically) break;
                        ((Flight)this.module).constMovementSpeed = 0.5 * ((Flight)this.module).speed.getValue();
                        break;
                    }
                    case 1: {
                        if (ListenerMove.mc.player.onGround && ListenerMove.mc.player.collidedVertically) {
                            double y;
                            ListenerMove.mc.player.motionY = y = 0.4;
                            event.setY(y);
                        }
                        ((Flight)this.module).constMovementSpeed *= 2.149;
                        break;
                    }
                    case 2: {
                        ((Flight)this.module).constMovementSpeed = 1.3 * ((Flight)this.module).speed.getValue();
                        break;
                    }
                    default: {
                        ((Flight)this.module).constMovementSpeed = ((Flight)this.module).lastDist - ((Flight)this.module).lastDist / 159.0;
                    }
                }
                MovementUtil.strafe(event, Math.max(((Flight)this.module).constMovementSpeed, MovementUtil.getSpeed()));
                ++((Flight)this.module).constNewStage;
                break;
            }
            case ConstoHareFast: {
                if (forward == 0.0f && strafe == 0.0f) {
                    event.setX(0.0);
                    event.setZ(0.0);
                }
                if (((Flight)this.module).oHareLevel != 1 || ListenerMove.mc.player.moveForward == 0.0f && ListenerMove.mc.player.moveStrafing == 0.0f) {
                    if (((Flight)this.module).oHareLevel == 2) {
                        ((Flight)this.module).oHareLevel = 3;
                        ((Flight)this.module).oHareMoveSpeed *= 2.1499999;
                    } else if (((Flight)this.module).oHareLevel == 3) {
                        ((Flight)this.module).oHareLevel = 4;
                        double difference = (ListenerMove.mc.player.ticksExisted % 2 == 0 ? 0.0103 : 0.0123) * (((Flight)this.module).oHareLastDist - MovementUtil.getSpeed());
                        ((Flight)this.module).oHareMoveSpeed = ((Flight)this.module).oHareLastDist - difference;
                    } else {
                        if (ListenerMove.mc.world.getCollisionBoxes((Entity)ListenerMove.mc.player, ListenerMove.mc.player.getEntityBoundingBox().offset(0.0, ListenerMove.mc.player.motionY, 0.0)).size() > 0 || ListenerMove.mc.player.collidedVertically) {
                            ((Flight)this.module).oHareLevel = 1;
                        }
                        ((Flight)this.module).oHareMoveSpeed = ((Flight)this.module).oHareLastDist - ((Flight)this.module).oHareLastDist / 159.0;
                    }
                } else {
                    ((Flight)this.module).oHareLevel = 2;
                    double boost = ListenerMove.mc.player.isPotionActive(MobEffects.SPEED) ? 1.56 : 2.034;
                    ((Flight)this.module).oHareMoveSpeed = boost * MovementUtil.getSpeed();
                }
                ((Flight)this.module).oHareMoveSpeed = Math.max(((Flight)this.module).oHareMoveSpeed, MovementUtil.getSpeed());
                MovementUtil.strafe(event, Math.max(((Flight)this.module).oHareMoveSpeed, MovementUtil.getSpeed()));
                break;
            }
            case Constantiam: {
                event.setX(event.getX() * ((Flight)this.module).speed.getValue());
                event.setZ(event.getZ() * ((Flight)this.module).speed.getValue());
                if (ListenerMove.mc.player.ticksExisted % 2 == 0) {
                    event.setY(0.00118212);
                } else {
                    event.setY(-0.00118212);
                }
                ++((Flight)this.module).constantiamStage;
                break;
            }
            case Normal: {
                event.setX(event.getX() * ((Flight)this.module).speed.getValue());
                event.setZ(event.getZ() * ((Flight)this.module).speed.getValue());
                break;
            }
            case AAC: {
                if (ListenerMove.mc.player.onGround || PositionUtil.inLiquid()) break;
                MovementUtil.strafe(event, 0.4521096646785736);
                break;
            }
            case Creative: {
                double speed = ((Flight)this.module).speed.getValue() / 10.0;
                if (ListenerMove.mc.player.movementInput.jump) {
                    event.setY(speed);
                    ListenerMove.mc.player.motionY = speed;
                } else if (ListenerMove.mc.player.movementInput.sneak) {
                    event.setY(-speed);
                    ListenerMove.mc.player.motionY = -speed;
                } else {
                    event.setY(0.0);
                    ListenerMove.mc.player.motionY = 0.0;
                    if (!ListenerMove.mc.player.collidedVertically && ((Flight)this.module).glide.getValue().booleanValue()) {
                        ListenerMove.mc.player.motionY -= ((Flight)this.module).glideSpeed.getValue().doubleValue();
                        event.setY(ListenerMove.mc.player.motionY);
                    }
                }
                MovementUtil.strafe(event, speed);
            }
        }
    }
}

