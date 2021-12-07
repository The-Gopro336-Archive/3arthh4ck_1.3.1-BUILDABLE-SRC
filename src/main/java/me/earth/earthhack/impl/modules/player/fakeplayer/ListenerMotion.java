/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 */
package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.Position;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

final class ListenerMotion
extends ModuleListener<FakePlayer, MotionUpdateEvent> {
    private boolean wasRecording;
    private int ticks;

    public ListenerMotion(FakePlayer module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        boolean record = ((FakePlayer)this.module).record.getValue();
        if (!record && this.wasRecording) {
            this.wasRecording = false;
        }
        if (((FakePlayer)this.module).gapple.getValue().booleanValue() && ((FakePlayer)this.module).timer.passed(((FakePlayer)this.module).gappleDelay.getValue().intValue())) {
            ((FakePlayer)this.module).fakePlayer.setAbsorptionAmount(16.0f);
            ((FakePlayer)this.module).fakePlayer.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
            ((FakePlayer)this.module).fakePlayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
            ((FakePlayer)this.module).fakePlayer.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
            ((FakePlayer)this.module).fakePlayer.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
            ((FakePlayer)this.module).timer.reset();
        }
        if (event.getStage() == Stage.PRE && !record) {
            if (((FakePlayer)this.module).playRecording.getValue().booleanValue()) {
                if (((FakePlayer)this.module).positions.isEmpty()) {
                    ModuleUtil.sendMessage((Module)this.module, "\u00a7cNo recording was found for this world!");
                    ((FakePlayer)this.module).playRecording.setValue(false);
                    return;
                }
                if (((FakePlayer)this.module).index >= ((FakePlayer)this.module).positions.size()) {
                    if (!((FakePlayer)this.module).loop.getValue().booleanValue()) {
                        ((FakePlayer)this.module).playRecording.setValue(false);
                    }
                    ((FakePlayer)this.module).index = 0;
                }
                if (this.ticks++ % 2 == 0) {
                    Position p = ((FakePlayer)this.module).positions.get(((FakePlayer)this.module).index++);
                    ((FakePlayer)this.module).fakePlayer.rotationYaw = p.getYaw();
                    ((FakePlayer)this.module).fakePlayer.rotationPitch = p.getPitch();
                    ((FakePlayer)this.module).fakePlayer.rotationYawHead = p.getHead();
                    ((FakePlayer)this.module).fakePlayer.setPositionAndRotationDirect(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(), 3, false);
                }
            } else {
                ((FakePlayer)this.module).index = 0;
            }
        } else if (event.getStage() == Stage.POST && record) {
            ((FakePlayer)this.module).playRecording.setValue(false);
            if (!this.wasRecording) {
                ModuleUtil.sendMessage((Module)this.module, "Recording...");
                ((FakePlayer)this.module).positions.clear();
                this.wasRecording = true;
            }
            if (this.ticks++ % 2 == 0) {
                ((FakePlayer)this.module).positions.add(new Position((EntityPlayer)ListenerMotion.mc.player));
            }
        }
    }
}

