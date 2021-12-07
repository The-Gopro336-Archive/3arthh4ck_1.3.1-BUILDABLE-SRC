/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemEndCrystal
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPlace
extends ModuleListener<Announcer, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerPlace(Announcer module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        CPacketPlayerTryUseItemOnBlock packet;
        ItemStack stack;
        if (((Announcer)this.module).place.getValue().booleanValue() && ((stack = ListenerPlace.mc.player.getHeldItem((packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand())).getItem() instanceof ItemBlock || stack.getItem() instanceof ItemEndCrystal)) {
            ((Announcer)this.module).addWordAndIncrement(AnnouncementType.Place, stack.getDisplayName());
        }
    }
}

