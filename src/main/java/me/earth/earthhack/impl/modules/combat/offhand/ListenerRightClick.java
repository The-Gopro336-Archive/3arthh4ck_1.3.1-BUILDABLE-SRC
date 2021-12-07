package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;

final class ListenerRightClick
extends ModuleListener<Offhand, ClickBlockEvent.Right> {
    public ListenerRightClick(Offhand module) {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event) {
        if (((Offhand)this.module).noOGC.getValue().booleanValue() && event.getHand() == EnumHand.MAIN_HAND) {
            Item mainHand = ListenerRightClick.mc.player.getHeldItemMainhand().getItem();
            Item offHand = ListenerRightClick.mc.player.getHeldItemOffhand().getItem();
            if (mainHand == Items.END_CRYSTAL && offHand == Items.GOLDEN_APPLE && event.getHand() == EnumHand.MAIN_HAND) {
                event.setCancelled(true);
                ListenerRightClick.mc.player.setActiveHand(EnumHand.OFF_HAND);
                ListenerRightClick.mc.playerController.processRightClick(ListenerRightClick.mc.player, ListenerRightClick.mc.world, EnumHand.OFF_HAND);
            }
        }
    }
}
