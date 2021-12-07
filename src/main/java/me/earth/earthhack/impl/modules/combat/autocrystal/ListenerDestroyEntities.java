/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketDestroyEntities
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerDestroyEntities(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        if (((AutoCrystal)this.module).destroyThread.getValue().booleanValue()) {
            ((AutoCrystal)this.module).threadHelper.schedulePacket(event);
        }
    }
}

