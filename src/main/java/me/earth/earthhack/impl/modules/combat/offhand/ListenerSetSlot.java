/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSetSlot
 */
package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot
extends ModuleListener<Offhand, PacketEvent.Receive<SPacketSetSlot>> {
    public ListenerSetSlot(Offhand module) {
        super(module, PacketEvent.Receive.class, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event) {
        ((Offhand)this.module).setSlotTimer.reset();
        if (!((Offhand)this.module).async.getValue().booleanValue() || ((Offhand)this.module).asyncTimer.passed(((Offhand)this.module).asyncCheck.getValue().intValue()) || ((Offhand)this.module).asyncSlot == -1 || ((SPacketSetSlot)event.getPacket()).getSlot() != ((Offhand)this.module).asyncSlot) {
            return;
        }
        event.setCancelled(true);
        ((Offhand)this.module).asyncSlot = -1;
    }
}

