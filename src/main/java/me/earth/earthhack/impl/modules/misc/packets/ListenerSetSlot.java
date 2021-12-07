/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainerCreative
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.server.SPacketSetSlot
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot
extends ModuleListener<Packets, PacketEvent.Receive<SPacketSetSlot>> {
    public ListenerSetSlot(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event) {
        if (!((Packets)this.module).fastSetSlot.getValue().booleanValue() || event.isCancelled()) {
            return;
        }
        EntityPlayerSP player = ListenerSetSlot.mc.player;
        if (player == null) {
            return;
        }
        int slot = ((SPacketSetSlot)event.getPacket()).getSlot();
        int id = ((SPacketSetSlot)event.getPacket()).getWindowId();
        ItemStack stack = ((SPacketSetSlot)event.getPacket()).getStack();
        if (id == -1) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> ListenerSetSlot.lambda$invoke$0((EntityPlayer)player, stack));
        } else if (id == -2) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> ListenerSetSlot.lambda$invoke$1((EntityPlayer)player, slot, stack));
        } else {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> ListenerSetSlot.lambda$invoke$2(id, slot, stack, (EntityPlayer)player));
        }
    }

    private static /* synthetic */ void lambda$invoke$2(int id, int slot, ItemStack stack, EntityPlayer player) {
        boolean badTab = false;
        GuiScreen current = ListenerSetSlot.mc.currentScreen;
        if (current instanceof GuiContainerCreative) {
            GuiContainerCreative creative = (GuiContainerCreative)current;
            boolean bl = badTab = creative.getSelectedTabIndex() != CreativeTabs.INVENTORY.getIndex();
        }
        if (id == 0 && slot >= 36 && slot < 45) {
            ItemStack inSlot;
            if (!stack.isEmpty() && ((inSlot = InventoryUtil.get(slot)).isEmpty() || inSlot.getCount() < stack.getCount())) {
                stack.setAnimationsToGo(5);
            }
            player.inventoryContainer.putStackInSlot(slot, stack);
            return;
        }
        Container container = player.openContainer;
        if (!(id != container.windowId || id == 0 && badTab)) {
            container.putStackInSlot(slot, stack);
        }
    }

    private static /* synthetic */ void lambda$invoke$1(EntityPlayer player, int slot, ItemStack stack) {
        player.inventory.setInventorySlotContents(slot, stack);
    }

    private static /* synthetic */ void lambda$invoke$0(EntityPlayer player, ItemStack stack) {
        player.inventory.setItemStack(stack);
    }
}

