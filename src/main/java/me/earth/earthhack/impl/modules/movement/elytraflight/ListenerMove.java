package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

final class ListenerMove
extends ModuleListener<ElytraFlight, MoveEvent> {
    public ListenerMove(ElytraFlight module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ItemStack stack = ListenerMove.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (stack.getItem() == Items.ELYTRA && ItemElytra.isUsable((ItemStack)stack)) {
            switch (((ElytraFlight)this.module).mode.getValue()) {
                case Wasp: {
                    if (!ListenerMove.mc.player.isElytraFlying()) {
                        return;
                    }
                    double vSpeed = ListenerMove.mc.gameSettings.keyBindJump.isKeyDown() ? ((ElytraFlight)this.module).vSpeed.getValue() : (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown() ? -((ElytraFlight)this.module).vSpeed.getValue().doubleValue() : 0.0);
                    event.setY(vSpeed);
                    ListenerMove.mc.player.setVelocity(0.0, 0.0, 0.0);
                    ListenerMove.mc.player.motionY = vSpeed;
                    ListenerMove.mc.player.moveVertical = (float)vSpeed;
                    if (MovementUtil.noMovementKeys() && !ListenerMove.mc.gameSettings.keyBindJump.isKeyDown() && !ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        event.setX(0.0);
                        event.setY(0.0);
                        event.setY(((ElytraFlight)this.module).antiKick.getValue() != false ? (double)(-((ElytraFlight)this.module).glide.getValue().floatValue()) : 0.0);
                        return;
                    }
                    MovementUtil.strafe(event, (double)((ElytraFlight)this.module).hSpeed.getValue());
                    break;
                }
                case Packet: {
                    if (ListenerMove.mc.player.onGround && ((ElytraFlight)this.module).noGround.getValue().booleanValue()) break;
                    if (((ElytraFlight)this.module).accel.getValue().booleanValue()) {
                        if (((ElytraFlight)this.module).lag) {
                            ((ElytraFlight)this.module).speed = 1.0;
                            ((ElytraFlight)this.module).lag = false;
                        }
                        if (((ElytraFlight)this.module).speed < ((ElytraFlight)this.module).hSpeed.getValue()) {
                            ((ElytraFlight)this.module).speed += 0.1;
                        }
                        if (((ElytraFlight)this.module).speed - 0.1 > ((ElytraFlight)this.module).hSpeed.getValue()) {
                            ((ElytraFlight)this.module).speed -= 0.1;
                        }
                    } else {
                        ((ElytraFlight)this.module).speed = ((ElytraFlight)this.module).hSpeed.getValue();
                    }
                    if (!MovementUtil.anyMovementKeys() && !ListenerMove.mc.player.collided && ((ElytraFlight)this.module).antiKick.getValue().booleanValue()) {
                        if (((ElytraFlight)this.module).timer.passed(1000L)) {
                            ((ElytraFlight)this.module).lag = true;
                            ListenerMove.mc.player.motionX += 0.03 * Math.sin(Math.toRadians(++((ElytraFlight)this.module).kick * 4));
                            ListenerMove.mc.player.motionZ += 0.03 * Math.cos(Math.toRadians(((ElytraFlight)this.module).kick * 4));
                        }
                    } else {
                        ((ElytraFlight)this.module).timer.reset();
                        ((ElytraFlight)this.module).lag = false;
                    }
                    if (((ElytraFlight)this.module).vertical.getValue().booleanValue() && ListenerMove.mc.player.movementInput.jump) {
                        ListenerMove.mc.player.motionY = ((ElytraFlight)this.module).vSpeed.getValue();
                        event.setY(((ElytraFlight)this.module).vSpeed.getValue());
                    } else if (ListenerMove.mc.player.movementInput.sneak) {
                        ListenerMove.mc.player.motionY = -((ElytraFlight)this.module).vSpeed.getValue().doubleValue();
                        event.setY(-((ElytraFlight)this.module).vSpeed.getValue().doubleValue());
                    } else if (((ElytraFlight)this.module).ncp.getValue().booleanValue()) {
                        if (ListenerMove.mc.player.ticksExisted % 32 != 0 || ((ElytraFlight)this.module).lag || !(Math.abs(event.getX()) >= 0.05) && !(Math.abs(event.getZ()) >= 0.05)) {
                            ListenerMove.mc.player.motionY = -2.0E-4;
                            event.setY(-2.0E-4);
                        } else {
                            ((ElytraFlight)this.module).speed -= ((ElytraFlight)this.module).speed / 2.0 * 0.1;
                            ListenerMove.mc.player.motionY = -2.0E-4;
                            event.setY(0.006200000000000001);
                        }
                    } else {
                        ListenerMove.mc.player.motionY = 0.0;
                        event.setY(0.0);
                    }
                    event.setX(event.getX() * (((ElytraFlight)this.module).lag ? 0.5 : ((ElytraFlight)this.module).speed));
                    event.setZ(event.getZ() * (((ElytraFlight)this.module).lag ? 0.5 : ((ElytraFlight)this.module).speed));
                    break;
                }
                case Boost: {
                    if (ListenerMove.mc.player.isElytraFlying() && ((ElytraFlight)this.module).noWater.getValue().booleanValue() && ListenerMove.mc.player.isInWater()) {
                        return;
                    }
                    if (!ListenerMove.mc.player.movementInput.jump || !ListenerMove.mc.player.isElytraFlying()) break;
                    float yaw = ListenerMove.mc.player.rotationYaw * ((float)Math.PI / 180);
                    ListenerMove.mc.player.motionX -= (double)(MathHelper.sin((float)yaw) * 0.15f);
                    ListenerMove.mc.player.motionZ += (double)(MathHelper.cos((float)yaw) * 0.15f);
                    break;
                }
                case Control: {
                    if (!ListenerMove.mc.player.isElytraFlying()) break;
                    if (!ListenerMove.mc.player.movementInput.forwardKeyDown && !ListenerMove.mc.player.movementInput.sneak) {
                        ListenerMove.mc.player.setVelocity(0.0, 0.0, 0.0);
                        break;
                    }
                    if (!ListenerMove.mc.player.movementInput.forwardKeyDown || !((ElytraFlight)this.module).vertical.getValue().booleanValue() && !(ListenerMove.mc.player.prevRotationPitch > 0.0f)) break;
                    float yaw = (float)Math.toRadians(ListenerMove.mc.player.rotationYaw);
                    double speed = ((ElytraFlight)this.module).hSpeed.getValue() / 10.0;
                    ListenerMove.mc.player.motionX = (double)MathHelper.sin((float)yaw) * -speed;
                    ListenerMove.mc.player.motionZ = (double)MathHelper.cos((float)yaw) * speed;
                    break;
                }
                case Normal: {
                    if (ListenerMove.mc.player.isElytraFlying() && ((ElytraFlight)this.module).noWater.getValue().booleanValue() && ListenerMove.mc.player.isInWater()) {
                        return;
                    }
                    if (ListenerMove.mc.player.movementInput.jump || !ListenerMove.mc.inGameHasFocus && ListenerMove.mc.player.isElytraFlying()) {
                        event.setY(0.0);
                    }
                    if (!ListenerMove.mc.inGameHasFocus || !((ElytraFlight)this.module).instant.getValue().booleanValue() || !ListenerMove.mc.player.movementInput.jump || ListenerMove.mc.player.isElytraFlying() || !((ElytraFlight)this.module).timer.passed(1000L)) break;
                    ListenerMove.mc.player.setJumping(false);
                    ListenerMove.mc.player.setSprinting(true);
                    ListenerMove.mc.player.jump();
                    ((ElytraFlight)this.module).sendFallPacket();
                    ((ElytraFlight)this.module).timer.reset();
                    return;
                }
            }
        }
    }
}
