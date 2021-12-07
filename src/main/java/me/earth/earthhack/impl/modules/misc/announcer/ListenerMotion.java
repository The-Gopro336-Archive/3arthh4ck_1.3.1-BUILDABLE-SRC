/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerMotion
extends ModuleListener<Announcer, MotionUpdateEvent> {
    public ListenerMotion(Announcer module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            String next;
            if (((Announcer)this.module).refresh.getValue().booleanValue()) {
                ((Announcer)this.module).reset();
                ((Announcer)this.module).loadFiles();
                ChatUtil.sendMessage("\u00a7a<\u00a7f" + ((Announcer)this.module).getDisplayName() + "\u00a7a" + "> Files loaded.");
                ((Announcer)this.module).refresh.setValue(false);
            }
            if (((Announcer)this.module).distance.getValue().booleanValue()) {
                ((Announcer)this.module).travelled += MovementUtil.getDistance2D();
            }
            if (((Announcer)this.module).autoEZ.getValue().booleanValue()) {
                EntityPlayer autoCrystal = Managers.TARGET.getAutoCrystal();
                Entity killAura = Managers.TARGET.getKillAura();
                if (autoCrystal != null) {
                    ((Announcer)this.module).targets.add(Managers.TARGET.getAutoCrystal());
                }
                if (killAura instanceof EntityPlayer) {
                    ((Announcer)this.module).targets.add((EntityPlayer)killAura);
                }
            }
            if (((Announcer)this.module).timer.passed(((Announcer)this.module).delay.getValue() * 1000.0) && (next = ((Announcer)this.module).getNextMessage()) != null) {
                ListenerMotion.mc.player.sendChatMessage(next);
                ((Announcer)this.module).timer.reset();
            }
        }
    }
}

