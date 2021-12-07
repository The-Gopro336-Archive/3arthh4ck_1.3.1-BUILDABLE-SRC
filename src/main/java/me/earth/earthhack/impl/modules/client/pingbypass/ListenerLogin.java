/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.login.client.CPacketLoginStart
 */
package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import net.minecraft.network.login.client.CPacketLoginStart;

final class ListenerLogin
extends ModuleListener<PingBypass, PacketEvent.Send<CPacketLoginStart>> {
    public ListenerLogin(PingBypass module) {
        super(module, PacketEvent.Send.class, CPacketLoginStart.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketLoginStart> event) {
        ((PingBypass)this.module).friendSerializer.clear();
        ((PingBypass)this.module).serializer.clear();
    }
}

