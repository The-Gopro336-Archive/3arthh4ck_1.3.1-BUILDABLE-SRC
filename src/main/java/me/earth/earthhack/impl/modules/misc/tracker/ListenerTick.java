/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerTick
extends ModuleListener<Tracker, TickEvent> {
    public ListenerTick(Tracker module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            if (((Tracker)this.module).isEnabled()) {
                int crystals;
                boolean found = false;
                for (EntityPlayer player : ListenerTick.mc.world.playerEntities) {
                    if (player == null || player.equals((Object)ListenerTick.mc.player) || player.equals((Object)RotationUtil.getRotationPlayer()) || PlayerUtil.isFakePlayer((Entity)player)) continue;
                    if (found && ((Tracker)this.module).only1v1.getValue().booleanValue()) {
                        ModuleUtil.disableRed((Module)this.module, "Disabled, you are not in a 1v1!");
                        return;
                    }
                    if (((Tracker)this.module).trackedPlayer == null) {
                        ModuleUtil.sendMessage((Module)this.module, "\u00a7dNow tracking \u00a75" + player.getName() + "\u00a7d" + "!");
                    }
                    ((Tracker)this.module).trackedPlayer = player;
                    found = true;
                }
                if (((Tracker)this.module).trackedPlayer == null) {
                    return;
                }
                int exp = ((Tracker)this.module).exp.get() / 64;
                if (((Tracker)this.module).expStacks != exp) {
                    ((Tracker)this.module).expStacks = exp;
                    ModuleUtil.sendMessage((Module)this.module, "\u00a75" + ((Tracker)this.module).trackedPlayer.getName() + "\u00a7d" + " used " + "\u00a7f" + exp + "\u00a7d" + (exp == 1 ? " stack" : " stacks") + " of Exp!", "Exp");
                }
                if (((Tracker)this.module).crystalStacks != (crystals = ((Tracker)this.module).crystals.get() / 64)) {
                    ((Tracker)this.module).crystalStacks = crystals;
                    ModuleUtil.sendMessage((Module)this.module, "\u00a75" + ((Tracker)this.module).trackedPlayer.getName() + "\u00a7d" + " used " + "\u00a7f" + crystals + "\u00a7d" + (crystals == 1 ? " stack" : " stacks") + " of Crystals!", "Crystals");
                }
            } else if (((Tracker)this.module).awaiting) {
                if (((Tracker)this.module).timer.passed(5000L)) {
                    ((Tracker)this.module).enable();
                    ((Tracker)this.module).awaiting = false;
                    return;
                }
                double time = MathUtil.round((double)(5000L - ((Tracker)this.module).timer.getTime()) / 1000.0, 1);
                ModuleUtil.sendMessage((Module)this.module, "\u00a7dDuel accepted. Tracker will enable in \u00a7f" + time + "\u00a7d" + " seconds!");
            }
        }
    }
}

