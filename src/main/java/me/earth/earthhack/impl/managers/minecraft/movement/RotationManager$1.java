package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

class RotationManager$1
extends EventListener<PacketEvent.Receive<SPacketPlayerPosLook>> {
    RotationManager$1(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            yaw += Globals.mc.player.rotationYaw;
        }
        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            pitch += Globals.mc.player.rotationPitch;
        }
        if (Globals.mc.player != null) {
            RotationManager.this.setServerRotations(yaw, pitch);
        }
    }
}
