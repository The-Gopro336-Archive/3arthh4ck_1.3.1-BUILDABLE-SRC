/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPotion
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumHand
 */
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
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;

final class ListenerTryUseItemOnBlock
extends ModuleListener<NoSlowDown, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerTryUseItemOnBlock(NoSlowDown module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        Item item = ListenerTryUseItemOnBlock.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        if (((NoSlowDown)this.module).superStrict.getValue().booleanValue() && (item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion)) {
            NetworkUtil.send(new CPacketHeldItemChange(ListenerTryUseItemOnBlock.mc.player.inventory.currentItem));
        }
    }
}

