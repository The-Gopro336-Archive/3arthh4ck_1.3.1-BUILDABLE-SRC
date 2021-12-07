package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketSpawnObject;

class ServerTickManager$2
extends EventListener<PacketEvent.Receive<SPacketSpawnObject>> {
    ServerTickManager$2(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (Globals.mc.world != null && Globals.mc.world.isRemote) {
            ServerTickManager.this.onSpawnObject();
        }
    }
}
