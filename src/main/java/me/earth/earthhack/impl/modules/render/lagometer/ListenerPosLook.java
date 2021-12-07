/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketPlayerPosLook$EnumFlags
 */
package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<LagOMeter, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(LagOMeter module) {
        super(module, PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        ((LagOMeter)this.module).teleported.set(false);
        EntityPlayer player = RotationUtil.getRotationPlayer();
        if (player == null) {
            return;
        }
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.X)) {
            x += player.posX;
        }
        if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Y)) {
            y += player.posY;
        }
        if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Z)) {
            z += player.posZ;
        }
        if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += player.rotationPitch;
        }
        if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += player.rotationYaw;
        }
        ((LagOMeter)this.module).x = x;
        ((LagOMeter)this.module).y = y;
        ((LagOMeter)this.module).z = z;
        ((LagOMeter)this.module).yaw = yaw;
        ((LagOMeter)this.module).pitch = pitch;
    }
}

