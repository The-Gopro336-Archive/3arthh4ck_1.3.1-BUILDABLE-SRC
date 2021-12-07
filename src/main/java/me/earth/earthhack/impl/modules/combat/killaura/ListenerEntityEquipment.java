/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemAir
 *  net.minecraft.item.ItemShield
 *  net.minecraft.network.play.server.SPacketEntityEquipment
 */
package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.server.SPacketEntityEquipment;

final class ListenerEntityEquipment
extends ModuleListener<KillAura, PacketEvent.Receive<SPacketEntityEquipment>> {
    public ListenerEntityEquipment(KillAura module) {
        super(module, PacketEvent.Receive.class, SPacketEntityEquipment.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityEquipment> event) {
        SPacketEntityEquipment packet = (SPacketEntityEquipment)event.getPacket();
        if (packet.getEquipmentSlot().getIndex() == 1 && ((KillAura)this.module).cancelEntityEquip.getValue().booleanValue() && packet.getItemStack().getItem() instanceof ItemAir && ListenerEntityEquipment.mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
            event.setCancelled(true);
        }
    }
}

