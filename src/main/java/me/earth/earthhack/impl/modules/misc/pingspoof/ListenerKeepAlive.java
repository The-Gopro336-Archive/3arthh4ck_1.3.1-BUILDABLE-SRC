/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketKeepAlive
 */
package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;

final class ListenerKeepAlive
extends ModuleListener<PingSpoof, PacketEvent.Send<CPacketKeepAlive>> {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);

    public ListenerKeepAlive(PingSpoof module) {
        super(module, PacketEvent.Send.class, CPacketKeepAlive.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketKeepAlive> event) {
        if (!PINGBYPASS.isEnabled() && ((PingSpoof)this.module).keepAlive.getValue().booleanValue()) {
            ((PingSpoof)this.module).onPacket((Packet<?>)event.getPacket());
            event.setCancelled(true);
        }
    }
}

