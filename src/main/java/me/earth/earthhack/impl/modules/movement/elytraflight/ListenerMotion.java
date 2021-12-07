/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemElytra
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.movement.elytraflight;

import java.util.Random;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

final class ListenerMotion
extends ModuleListener<ElytraFlight, MotionUpdateEvent> {
    private static final Random RANDOM = new Random();
    private static float previousTimerVal = -1.0f;

    public ListenerMotion(ElytraFlight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }
        ItemStack stack = ListenerMotion.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (stack.getItem() != Items.ELYTRA || !ItemElytra.isUsable((ItemStack)stack)) {
            return;
        }
        if (ListenerMotion.mc.player.isElytraFlying() && (((ElytraFlight)this.module).noWater.getValue().booleanValue() && ListenerMotion.mc.player.isInWater() || ((ElytraFlight)this.module).noGround.getValue().booleanValue() && ListenerMotion.mc.player.onGround)) {
            ((ElytraFlight)this.module).sendFallPacket();
            return;
        }
        if (((ElytraFlight)this.module).mode.getValue() == ElytraMode.Packet) {
            boolean falling = false;
            if (((ElytraFlight)this.module).infDura.getValue().booleanValue() || !ListenerMotion.mc.player.isElytraFlying()) {
                ((ElytraFlight)this.module).sendFallPacket();
                falling = true;
            }
            if (((ElytraFlight)this.module).ncp.getValue().booleanValue() && !((ElytraFlight)this.module).lag && (Math.abs(event.getX()) >= 0.05 || Math.abs(event.getZ()) >= 0.05)) {
                double y = 1.0E-8 + 1.0E-8 * (1.0 + (double)RANDOM.nextInt(1 + (RANDOM.nextBoolean() ? RANDOM.nextInt(34) : RANDOM.nextInt(43))));
                if (ListenerMotion.mc.player.onGround || ListenerMotion.mc.player.ticksExisted % 2 == 0) {
                    event.setY(event.getY() + y);
                    return;
                }
                event.setY(event.getY() - y);
                return;
            }
            if (falling) {
                return;
            }
        }
        if (((ElytraFlight)this.module).autoStart.getValue().booleanValue() && ListenerMotion.mc.gameSettings.keyBindJump.isKeyDown() && !ListenerMotion.mc.player.isElytraFlying() && ListenerMotion.mc.player.motionY < 0.0) {
            if (previousTimerVal == -1.0f) {
                previousTimerVal = Managers.TIMER.getSpeed();
            }
            Managers.TIMER.setTimer(0.17f);
            if (((ElytraFlight)this.module).timer.passed(10L)) {
                ((IKeyBinding)ListenerMotion.mc.gameSettings.keyBindJump).setPressed(true);
                ((ElytraFlight)this.module).sendFallPacket();
                ((ElytraFlight)this.module).timer.reset();
            } else {
                ((IKeyBinding)ListenerMotion.mc.gameSettings.keyBindJump).setPressed(false);
            }
            return;
        }
        if (previousTimerVal != -1.0f) {
            Managers.TIMER.setTimer(previousTimerVal);
            previousTimerVal = -1.0f;
        }
        if (((ElytraFlight)this.module).infDura.getValue().booleanValue() && ListenerMotion.mc.player.isElytraFlying()) {
            ((ElytraFlight)this.module).sendFallPacket();
        }
    }
}

