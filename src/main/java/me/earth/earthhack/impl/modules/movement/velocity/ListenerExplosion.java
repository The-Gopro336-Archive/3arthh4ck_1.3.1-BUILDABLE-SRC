package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketExplosion;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion
extends ModuleListener<Velocity, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(Velocity module) {
        super(module, PacketEvent.Receive.class, -1000000, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (((Velocity)this.module).explosions.getValue().booleanValue()) {
            ISPacketExplosion explosion = (ISPacketExplosion)event.getPacket();
            explosion.setX(explosion.getX() * ((Velocity)this.module).horizontal.getValue().floatValue());
            explosion.setY(explosion.getY() * ((Velocity)this.module).horizontal.getValue().floatValue());
            explosion.setZ(explosion.getZ() * ((Velocity)this.module).horizontal.getValue().floatValue());
        }
    }
}
