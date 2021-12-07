package me.earth.earthhack.impl.util.minecraft;

import java.util.Objects;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class MovementUtil
implements Globals {
    public static boolean isMoving() {
        return (double)MovementUtil.mc.player.moveForward != 0.0 || (double)MovementUtil.mc.player.moveStrafing != 0.0;
    }

    public static boolean anyMovementKeys() {
        return MovementUtil.mc.player.movementInput.forwardKeyDown || MovementUtil.mc.player.movementInput.backKeyDown || MovementUtil.mc.player.movementInput.leftKeyDown || MovementUtil.mc.player.movementInput.rightKeyDown || MovementUtil.mc.player.movementInput.jump || MovementUtil.mc.player.movementInput.sneak;
    }

    public static boolean noMovementKeys() {
        return !MovementUtil.mc.player.movementInput.forwardKeyDown && !MovementUtil.mc.player.movementInput.backKeyDown && !MovementUtil.mc.player.movementInput.rightKeyDown && !MovementUtil.mc.player.movementInput.leftKeyDown;
    }

    public static boolean noMovementKeysOrJump() {
        return MovementUtil.noMovementKeys() && !Keyboard.isKeyDown(MovementUtil.mc.gameSettings.keyBindJump.getKeyCode());
    }

    public static void setMoveSpeed(double speed) {
        double forward = MovementUtil.mc.player.movementInput.moveForward;
        double strafe = MovementUtil.mc.player.movementInput.moveStrafe;
        float yaw = MovementUtil.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtil.mc.player.motionX = 0.0;
            MovementUtil.mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MovementUtil.mc.player.motionX = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            MovementUtil.mc.player.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
        }
    }

    public static void strafe(MoveEvent event, double speed) {
        if (MovementUtil.isMoving()) {
            double[] strafe = MovementUtil.strafe(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        } else {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static double[] strafe(double speed) {
        return MovementUtil.strafe(MovementUtil.mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed) {
        return MovementUtil.strafe(entity, MovementUtil.mc.player.movementInput, speed);
    }

    public static double[] strafe(Entity entity, MovementInput movementInput, double speed) {
        float moveForward = movementInput.moveForward;
        float moveStrafe = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static MovementInput inverse(Entity entity, double speed) {
        MovementInput input = new MovementInput();
        input.sneak = entity.isSneaking();
        block0: for (float d = -1.0f; d <= 1.0f; d += 1.0f) {
            for (float e = -1.0f; e <= 1.0f; e += 1.0f) {
                MovementInput dummyInput = new MovementInput();
                dummyInput.moveForward = d;
                dummyInput.moveStrafe = e;
                dummyInput.sneak = entity.isSneaking();
                double[] moveVec = MovementUtil.strafe(entity, dummyInput, speed);
                if (entity.isSneaking()) {
                    moveVec[0] = moveVec[0] * (double)0.3f;
                    moveVec[1] = moveVec[1] * (double)0.3f;
                }
                double targetMotionX = moveVec[0];
                double targetMotionZ = moveVec[1];
                if (!(targetMotionX < 0.0 ? entity.motionX <= targetMotionX : entity.motionX >= targetMotionX) || !(targetMotionZ < 0.0 ? entity.motionZ <= targetMotionZ : entity.motionZ >= targetMotionZ)) continue;
                input.moveForward = d;
                input.moveStrafe = e;
                continue block0;
            }
        }
        return input;
    }

    public static double getDistance2D() {
        double xDist = MovementUtil.mc.player.posX - MovementUtil.mc.player.prevPosX;
        double zDist = MovementUtil.mc.player.posZ - MovementUtil.mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double getDistance3D() {
        double xDist = MovementUtil.mc.player.posX - MovementUtil.mc.player.prevPosX;
        double yDist = MovementUtil.mc.player.posY - MovementUtil.mc.player.prevPosY;
        double zDist = MovementUtil.mc.player.posZ - MovementUtil.mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public static double getSpeed() {
        return MovementUtil.getSpeed(false);
    }

    public static double getSpeed(boolean slowness, double defaultSpeed) {
        int amplifier;
        if (MovementUtil.mc.player.isPotionActive(MobEffects.SPEED)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (slowness && MovementUtil.mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getSpeed(boolean slowness) {
        int amplifier;
        double defaultSpeed = 0.2873;
        if (MovementUtil.mc.player.isPotionActive(MobEffects.SPEED)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (slowness && MovementUtil.mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0;
        if (MovementUtil.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            int amplifier = MovementUtil.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (double)(amplifier + 1) * 0.1;
        }
        return defaultSpeed;
    }

    public static boolean isInMovementDirection(double x, double y, double z) {
        if (MovementUtil.mc.player.motionX != 0.0 || MovementUtil.mc.player.motionZ != 0.0) {
            BlockPos movingPos = new BlockPos(MovementUtil.mc.player).add(MovementUtil.mc.player.motionX * 10000.0, 0.0, MovementUtil.mc.player.motionZ * 10000.0);
            BlockPos antiPos = new BlockPos(MovementUtil.mc.player).add(MovementUtil.mc.player.motionX * -10000.0, 0.0, MovementUtil.mc.player.motionY * -10000.0);
            return movingPos.distanceSq(x, y, z) < antiPos.distanceSq(x, y, z);
        }
        return true;
    }

    public static void constantiamDamage() {
    }
}
