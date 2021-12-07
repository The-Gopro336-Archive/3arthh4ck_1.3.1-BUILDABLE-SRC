/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EnumPlayerModelParts
 */
package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.skinblink.SkinBlink;
import net.minecraft.entity.player.EnumPlayerModelParts;

final class ListenerGameLoop
extends ModuleListener<SkinBlink, GameLoopEvent> {
    public ListenerGameLoop(SkinBlink module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (((SkinBlink)this.module).timer.passed(((SkinBlink)this.module).delay.getValue().intValue())) {
            for (EnumPlayerModelParts parts : EnumPlayerModelParts.values()) {
                ListenerGameLoop.mc.gameSettings.setModelPartEnabled(parts, ((SkinBlink)this.module).random.getValue().booleanValue() ? Math.random() < 0.5 : !ListenerGameLoop.mc.gameSettings.getModelParts().contains((Object)parts));
            }
            ((SkinBlink)this.module).timer.reset();
        }
    }
}

