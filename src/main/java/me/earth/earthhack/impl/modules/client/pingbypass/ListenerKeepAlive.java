package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import net.minecraft.network.play.server.SPacketKeepAlive;

final class ListenerKeepAlive
extends ModuleListener<PingBypass, PacketEvent.Receive<SPacketKeepAlive>> {
    public ListenerKeepAlive(PingBypass module) {
        super(module, PacketEvent.Receive.class, SPacketKeepAlive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketKeepAlive> event) {
        SPacketKeepAlive packet = (SPacketKeepAlive)event.getPacket();
        if (!((PingBypass)this.module).handled && packet.getId() > 0L && packet.getId() < 1000L) {
            ((PingBypass)this.module).startTime = System.currentTimeMillis() - ((PingBypass)this.module).startTime;
            ((PingBypass)this.module).serverPing = (int)packet.getId();
            ((PingBypass)this.module).ping = ((PingBypass)this.module).startTime;
            ((PingBypass)this.module).handled = true;
            event.setCancelled(true);
        }
    }
}
