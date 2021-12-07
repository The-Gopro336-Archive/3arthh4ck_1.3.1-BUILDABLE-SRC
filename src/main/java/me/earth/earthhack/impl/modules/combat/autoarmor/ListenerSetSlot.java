/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.INetHandlerPlayClient
 *  net.minecraft.network.play.server.SPacketSetSlot
 */
package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DesyncClick;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot
extends ModuleListener<AutoArmor, PacketEvent.Receive<SPacketSetSlot>> {
    public ListenerSetSlot(AutoArmor module) {
        super(module, PacketEvent.Receive.class, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event) {
        SPacketSetSlot packet = (SPacketSetSlot)event.getPacket();
        if (!((AutoArmor)this.module).noDuraDesync.getValue().booleanValue() || event.isCancelled() || packet.getWindowId() != 0 || packet.getSlot() < 5 || packet.getSlot() > 8) {
            return;
        }
        event.setCancelled(true);
        mc.addScheduledTask(() -> {
            if (ListenerSetSlot.mc.player == null) {
                return;
            }
            DesyncClick click = ((AutoArmor)this.module).desyncMap.get(packet.getSlot());
            if (click == null || System.currentTimeMillis() - click.getTimeStamp() > (long)((AutoArmor)this.module).removeTime.getValue().intValue()) {
                packet.processPacket((INetHandlerPlayClient)ListenerSetSlot.mc.player.connection);
            } else {
                ItemStack stack = InventoryUtil.get(packet.getSlot());
                if (InventoryUtil.equals(stack, packet.getStack())) {
                    packet.processPacket((INetHandlerPlayClient)ListenerSetSlot.mc.player.connection);
                    return;
                }
                ItemStack drag = ListenerSetSlot.mc.player.inventory.getItemStack();
                if (InventoryUtil.equals(drag, packet.getStack())) {
                    ListenerSetSlot.mc.player.inventory.setItemStack(packet.getStack());
                    return;
                }
                int slot = click.getClick().getTarget();
                if (slot > 0 && slot < 45 && InventoryUtil.equals(stack = InventoryUtil.get(slot), packet.getStack())) {
                    InventoryUtil.put(slot, packet.getStack());
                }
            }
        });
    }
}

