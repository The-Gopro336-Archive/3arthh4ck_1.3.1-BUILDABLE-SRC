/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 */
package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerMotion
extends ModuleListener<BowSpam, MotionUpdateEvent> {
    private float lastTimer = -1.0f;

    public ListenerMotion(BowSpam module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            ItemStack stack = this.getStack();
            if (((BowSpam)this.module).rape.getValue().booleanValue()) {
                if (ListenerMotion.mc.player.onGround) {
                    if (stack != null && !ListenerMotion.mc.player.getActiveItemStack().isEmpty() && ListenerMotion.mc.player.getItemInUseCount() > 0) {
                        Managers.TIMER.setTimer(6.0f);
                        if (stack.getMaxItemUseDuration() - ListenerMotion.mc.player.getItemInUseCount() > ((BowSpam)this.module).delay.getValue() * 6) {
                            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> ListenerMotion.mc.playerController.onStoppedUsingItem((EntityPlayer)ListenerMotion.mc.player));
                        }
                    } else {
                        if (this.lastTimer > 0.0f && Managers.TIMER.getSpeed() != this.lastTimer) {
                            Managers.TIMER.setTimer(this.lastTimer);
                        }
                        this.lastTimer = Managers.TIMER.getSpeed();
                    }
                }
            } else {
                if (this.lastTimer > 0.0f && Managers.TIMER.getSpeed() != this.lastTimer) {
                    Managers.TIMER.setTimer(this.lastTimer);
                    this.lastTimer = 1.0f;
                }
                if (stack != null && !ListenerMotion.mc.player.getActiveItemStack().isEmpty()) {
                    float f = stack.getMaxItemUseDuration() - ListenerMotion.mc.player.getItemInUseCount();
                    float f2 = ((BowSpam)this.module).tpsSync.getValue() != false ? 20.0f - Managers.TPS.getTps() : 0.0f;
                    if (f - f2 >= (float)((BowSpam)this.module).delay.getValue().intValue()) {
                        if (((BowSpam)this.module).bowBomb.getValue().booleanValue()) {
                            NetworkUtil.sendPacketNoEvent(new CPacketPlayer.PositionRotation(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY - 0.0624, ListenerMotion.mc.player.posZ, ListenerMotion.mc.player.rotationYaw, ListenerMotion.mc.player.rotationPitch, false));
                            NetworkUtil.sendPacketNoEvent(new CPacketPlayer.PositionRotation(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY - 999.0, ListenerMotion.mc.player.posZ, ListenerMotion.mc.player.rotationYaw, ListenerMotion.mc.player.rotationPitch, true));
                        }
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> ListenerMotion.mc.playerController.onStoppedUsingItem((EntityPlayer)ListenerMotion.mc.player));
                    }
                }
            }
        }
    }

    private ItemStack getStack() {
        ItemStack mainHand = ListenerMotion.mc.player.getHeldItemMainhand();
        if (mainHand.getItem() instanceof ItemBow) {
            return mainHand;
        }
        ItemStack offHand = ListenerMotion.mc.player.getHeldItemOffhand();
        if (offHand.getItem() instanceof ItemBow) {
            return offHand;
        }
        return null;
    }
}

