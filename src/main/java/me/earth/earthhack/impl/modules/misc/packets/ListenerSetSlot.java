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
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> player.inventory.setItemStack(stack));
        } else if (id == -2) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> player.inventory.setInventorySlotContents(slot, stack));
        } else {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                boolean badTab = false;
                GuiScreen current = ListenerSetSlot.mc.currentScreen;
                if (current instanceof GuiContainerCreative) {
                    GuiContainerCreative creative = (GuiContainerCreative)((Object)current);
                    boolean bl = badTab = creative.getSelectedTabIndex() != CreativeTabs.INVENTORY.getTabIndex();
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
            });
        }
    }
}
