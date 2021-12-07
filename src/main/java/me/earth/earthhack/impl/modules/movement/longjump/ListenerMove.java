/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;

final class ListenerMove
extends ModuleListener<LongJump, MoveEvent> {
    public ListenerMove(LongJump module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((LongJump)this.module).mode.getValue() == JumpMode.Normal) {
            if (ListenerMove.mc.player.moveStrafing <= 0.0f && ListenerMove.mc.player.moveForward <= 0.0f) {
                ((LongJump)this.module).stage = 1;
            }
            if (MathUtil.round(ListenerMove.mc.player.posY - (double)((int)ListenerMove.mc.player.posY), 3) == MathUtil.round(0.943, 3)) {
                ListenerMove.mc.player.motionY -= 0.03;
                event.setY(event.getY() - 0.03);
            }
            if (((LongJump)this.module).stage == 1 && MovementUtil.isMoving()) {
                ((LongJump)this.module).stage = 2;
                ((LongJump)this.module).speed = ((LongJump)this.module).boost.getValue() * MovementUtil.getSpeed() - 0.01;
            } else if (((LongJump)this.module).stage == 2) {
                ((LongJump)this.module).stage = 3;
                ListenerMove.mc.player.motionY = 0.424;
                event.setY(0.424);
                ((LongJump)this.module).speed *= 2.149802;
            } else if (((LongJump)this.module).stage == 3) {
                ((LongJump)this.module).stage = 4;
                double difference = 0.66 * (((LongJump)this.module).distance - MovementUtil.getSpeed());
                ((LongJump)this.module).speed = ((LongJump)this.module).distance - difference;
            } else {
                if (ListenerMove.mc.world.getCollisionBoxes((Entity)ListenerMove.mc.player, ListenerMove.mc.player.getEntityBoundingBox().offset(0.0, ListenerMove.mc.player.motionY, 0.0)).size() > 0 || ListenerMove.mc.player.collidedVertically) {
                    ((LongJump)this.module).stage = 1;
                }
                ((LongJump)this.module).speed = ((LongJump)this.module).distance - ((LongJump)this.module).distance / 159.0;
            }
            ((LongJump)this.module).speed = Math.max(((LongJump)this.module).speed, MovementUtil.getSpeed());
            MovementUtil.strafe(event, ((LongJump)this.module).speed);
            float moveForward = ListenerMove.mc.player.movementInput.moveForward;
            float moveStrafe = ListenerMove.mc.player.movementInput.moveStrafe;
            float rotationYaw = ListenerMove.mc.player.rotationYaw;
            if (moveForward == 0.0f && moveStrafe == 0.0f) {
                event.setX(0.0);
                event.setZ(0.0);
            } else if (moveForward != 0.0f) {
                if (moveStrafe >= 1.0f) {
                    rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
                    moveStrafe = 0.0f;
                } else if (moveStrafe <= -1.0f) {
                    rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
                    moveStrafe = 0.0f;
                }
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
            event.setX((double)moveForward * ((LongJump)this.module).speed * cos + (double)moveStrafe * ((LongJump)this.module).speed * sin);
            event.setZ((double)moveForward * ((LongJump)this.module).speed * sin - (double)moveStrafe * ((LongJump)this.module).speed * cos);
        }
    }
}

