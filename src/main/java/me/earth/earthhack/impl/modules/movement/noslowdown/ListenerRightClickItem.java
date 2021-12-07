package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;

final class ListenerRightClickItem
extends ModuleListener<NoSlowDown, RightClickItemEvent> {
    public ListenerRightClickItem(NoSlowDown module) {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event) {
        Item item = ListenerRightClickItem.mc.player.getHeldItem(event.getHand()).getItem();
        if (!(((NoSlowDown)this.module).sneakPacket.getValue().booleanValue() && (item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion) && Managers.ACTION.isSneaking())) {
            // empty if block
        }
    }
}
