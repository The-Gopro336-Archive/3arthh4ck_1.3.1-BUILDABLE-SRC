package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

class PositionManager$1
extends EventListener<PacketEvent.Receive<SPacketPlayerPosLook>> {
    PositionManager$1(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
            x += Globals.mc.player.posX;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
            y += Globals.mc.player.posY;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
            z += Globals.mc.player.posZ;
        }
        PositionManager.this.last_x = MathHelper.clamp((double)x, (double)-3.0E7, (double)3.0E7);
        PositionManager.this.last_y = y;
        PositionManager.this.last_z = MathHelper.clamp((double)z, (double)-3.0E7, (double)3.0E7);
        PositionManager.this.onGround = false;
        PositionManager.this.teleportID = packet.getTeleportId();
    }
}
