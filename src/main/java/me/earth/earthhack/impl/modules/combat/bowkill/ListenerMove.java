/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBow
 */
package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import net.minecraft.item.ItemBow;

final class ListenerMove
extends ModuleListener<BowKiller, MoveEvent> {
    public ListenerMove(BowKiller module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (!ListenerMove.mc.player.collidedVertically) {
            return;
        }
        if (((BowKiller)this.module).staticS.getValue().booleanValue() && ListenerMove.mc.player.getActiveItemStack().getItem() instanceof ItemBow && ((BowKiller)this.module).blockUnder) {
            ListenerMove.mc.player.setVelocity(0.0, 0.0, 0.0);
            event.setX(0.0);
            event.setY(0.0);
            event.setZ(0.0);
        }
    }
}

