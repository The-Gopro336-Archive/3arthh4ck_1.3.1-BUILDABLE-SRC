package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

final class ListenerTryUseItem
extends ModuleListener<NoSlowDown, PacketEvent.Post<CPacketPlayerTryUseItem>> {
    public ListenerTryUseItem(NoSlowDown module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItem> event) {
        Item item = ListenerTryUseItem.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        if (((NoSlowDown)this.module).superStrict.getValue().booleanValue() && (item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion)) {
            NetworkUtil.send(new CPacketHeldItemChange(ListenerTryUseItem.mc.player.inventory.currentItem));
        }
    }
}
