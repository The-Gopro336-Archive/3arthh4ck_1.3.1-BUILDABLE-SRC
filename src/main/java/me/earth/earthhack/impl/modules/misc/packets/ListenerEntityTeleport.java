package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.network.play.server.SPacketEntityTeleport;

final class ListenerEntityTeleport
extends ModuleListener<Packets, PacketEvent.Receive<SPacketEntityTeleport>> {
    public ListenerEntityTeleport(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketEntityTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityTeleport> event) {
        if (event.isCancelled() || !((Packets)this.module).fastEntityTeleport.getValue().booleanValue()) {
            return;
        }
        SPacketEntityTeleport packet = (SPacketEntityTeleport)event.getPacket();
        Entity e = Managers.ENTITIES.getEntity(packet.getEntityId());
        if (e == null) {
            return;
        }
        event.setCancelled(((Packets)this.module).cancelEntityTeleport.getValue());
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EntityTracker.updateServerPosition((Entity)e, (double)x, (double)y, (double)z);
        if (!e.canPassengerSteer()) {
            float yaw = (float)(packet.getYaw() * 360) / 256.0f;
            float pitch = (float)(packet.getPitch() * 360) / 256.0f;
            if (Math.abs(e.posX - x) < 0.03125 && Math.abs(e.posY - y) < 0.015625 && Math.abs(e.posZ - z) < 0.03125) {
                if (((Packets)this.module).miniTeleports.getValue().booleanValue() && ((Packets)this.module).cancelEntityTeleport.getValue().booleanValue()) {
                    e.setPositionAndRotation(x, y, z, yaw, pitch);
                    return;
                }
                e.setPositionAndRotationDirect(e.posX, e.posY, e.posZ, yaw, pitch, 0, true);
            } else {
                e.setPositionAndRotationDirect(x, y, z, yaw, pitch, 3, true);
            }
            e.onGround = packet.getOnGround();
        }
    }
}
