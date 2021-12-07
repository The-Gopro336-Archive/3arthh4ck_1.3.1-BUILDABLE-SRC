/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        ((AutoCrystal)this.module).rotationCanceller.drop();
    }
}

