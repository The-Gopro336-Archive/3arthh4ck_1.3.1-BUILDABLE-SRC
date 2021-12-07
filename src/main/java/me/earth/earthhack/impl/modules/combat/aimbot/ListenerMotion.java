/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBow
 */
package me.earth.earthhack.impl.modules.combat.aimbot;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.aimbot.AimBot;
import net.minecraft.item.ItemBow;

final class ListenerMotion
extends ModuleListener<AimBot, MotionUpdateEvent> {
    public ListenerMotion(AimBot module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }
        if (!(ListenerMotion.mc.player.getActiveItemStack().getItem() instanceof ItemBow)) {
            ((AimBot)this.module).target = null;
            return;
        }
        ((AimBot)this.module).target = ((AimBot)this.module).getTarget();
        if (((AimBot)this.module).target == null) {
            return;
        }
    }
}

