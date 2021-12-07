package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import net.minecraft.init.Items;

final class ListenerRightClick
extends ModuleListener<BowKiller, RightClickItemEvent> {
    public ListenerRightClick(BowKiller module) {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event) {
        if (!ListenerRightClick.mc.player.collidedVertically) {
            return;
        }
        if (ListenerRightClick.mc.player.getHeldItem(event.getHand()).getItem() == Items.BOW && ((BowKiller)this.module).blockUnder) {
            ((BowKiller)this.module).cancelling = true;
            ((BowKiller)this.module).needsMessage = true;
        }
    }
}
