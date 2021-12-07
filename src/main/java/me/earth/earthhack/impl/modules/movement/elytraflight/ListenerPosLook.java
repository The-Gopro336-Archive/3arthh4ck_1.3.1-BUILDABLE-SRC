/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 */
package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<ElytraFlight, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(ElytraFlight module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (((ElytraFlight)this.module).mode.getValue() == ElytraMode.Packet && ListenerPosLook.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            ((ElytraFlight)this.module).lag = true;
        }
    }
}

