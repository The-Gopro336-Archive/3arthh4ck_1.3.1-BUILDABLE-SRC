/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumHand
 */
package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;
import net.minecraft.util.EnumHand;

final class ListenerGameLoop
extends ModuleListener<NoAFK, GameLoopEvent> {
    public ListenerGameLoop(NoAFK module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (ListenerGameLoop.mc.player != null) {
            if (((NoAFK)this.module).rotate.getValue().booleanValue()) {
                ListenerGameLoop.mc.player.rotationYaw = (float)((double)ListenerGameLoop.mc.player.rotationYaw + 0.003);
            }
            if (((NoAFK)this.module).swing.getValue().booleanValue() && ((NoAFK)this.module).swing_timer.passed(2000L)) {
                ListenerGameLoop.mc.player.swingArm(EnumHand.MAIN_HAND);
                ((NoAFK)this.module).swing_timer.reset();
            }
        }
    }
}

