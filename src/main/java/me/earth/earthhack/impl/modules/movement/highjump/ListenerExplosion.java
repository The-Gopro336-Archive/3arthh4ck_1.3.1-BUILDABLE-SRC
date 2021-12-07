package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.highjump.HighJump;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion
extends ModuleListener<HighJump, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(HighJump module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (((HighJump)this.module).explosions.getValue().booleanValue()) {
            double y = ((SPacketExplosion)event.getPacket()).getMotionY();
            ((HighJump)this.module).addVelocity(y);
        }
    }
}
