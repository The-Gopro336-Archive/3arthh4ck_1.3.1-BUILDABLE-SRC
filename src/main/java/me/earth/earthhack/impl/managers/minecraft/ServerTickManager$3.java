package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;

class ServerTickManager$3
extends EventListener<PacketEvent.Receive<SPacketTimeUpdate>> {
    ServerTickManager$3(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketTimeUpdate> event) {
        if (Globals.mc.world != null && Globals.mc.world.isRemote) {
            ServerTickManager.this.reset();
        }
    }
}
