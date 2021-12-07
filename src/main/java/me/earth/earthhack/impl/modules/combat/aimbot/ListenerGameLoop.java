/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBow
 */
package me.earth.earthhack.impl.modules.combat.aimbot;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.aimbot.AimBot;
import net.minecraft.item.ItemBow;

final class ListenerGameLoop
extends ModuleListener<AimBot, GameLoopEvent> {
    public ListenerGameLoop(AimBot module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (((AimBot)this.module).target != null && !((AimBot)this.module).silent.getValue().booleanValue() && ListenerGameLoop.mc.player.getActiveItemStack().getItem() instanceof ItemBow) {
            ListenerGameLoop.mc.player.rotationYaw = ((AimBot)this.module).yaw;
            ListenerGameLoop.mc.player.rotationPitch = ((AimBot)this.module).pitch;
        }
    }
}

