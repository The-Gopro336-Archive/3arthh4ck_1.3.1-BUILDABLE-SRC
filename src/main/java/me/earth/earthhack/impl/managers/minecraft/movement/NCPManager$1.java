package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

class NCPManager$1
extends EventListener<PacketEvent.Receive<SPacketPlayerPosLook>> {
    NCPManager$1(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        NCPManager.this.lagTimer.set(System.currentTimeMillis());
    }
}
