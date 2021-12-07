/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBow
 */
package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import net.minecraft.item.ItemBow;

final class ListenerUseItem
extends ModuleListener<Arrows, RightClickItemEvent> {
    public ListenerUseItem(Arrows module) {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event) {
        if (!(!(ListenerUseItem.mc.player.getHeldItem(event.getHand()).getItem() instanceof ItemBow) || ((Arrows)this.module).cancelTime.getValue() == 0 || ((Arrows)this.module).timer.passed(((Arrows)this.module).cancelTime.getValue().intValue()) || ((Arrows)this.module).preCycle.getValue().booleanValue() && !((Arrows)this.module).fastCancel.getValue().booleanValue() && ((Arrows)this.module).fast)) {
            event.setCancelled(true);
        }
    }
}

