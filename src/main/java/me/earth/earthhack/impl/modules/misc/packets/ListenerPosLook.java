/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketPlayerPosLook$EnumFlags
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

final class ListenerPosLook
extends ModuleListener<Packets, PacketEvent.Receive<SPacketPlayerPosLook>> {
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);
    private static final ModuleCache<Freecam> FREE_CAM = Caches.getModule(Freecam.class);

    public ListenerPosLook(Packets module) {
        super(module, PacketEvent.Receive.class, -1000, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (event.isCancelled() || ListenerPosLook.mc.player == null || PACKET_FLY.isEnabled() || FREE_CAM.isEnabled() || !((Packets)this.module).fastTeleports.getValue().booleanValue()) {
            return;
        }
        event.setCancelled(true);
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        boolean xFlag = packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.X);
        boolean yFlag = packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Y);
        boolean zFlag = packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Z);
        boolean yawFlag = packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Y_ROT);
        boolean pitFlag = packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.X_ROT);
        double x = packet.getX() + (xFlag ? ListenerPosLook.mc.player.posX : 0.0);
        double y = packet.getY() + (yFlag ? ListenerPosLook.mc.player.posY : 0.0);
        double z = packet.getZ() + (zFlag ? ListenerPosLook.mc.player.posZ : 0.0);
        float yaw = packet.getYaw() + (yawFlag ? ListenerPosLook.mc.player.rotationYaw : 0.0f);
        float pit = packet.getPitch() + (pitFlag ? ListenerPosLook.mc.player.rotationPitch : 0.0f);
        ListenerPosLook.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(packet.getTeleportId()));
        Managers.ROTATION.setBlocking(true);
        ListenerPosLook.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(MathHelper.clamp((double)x, (double)-3.0E7, (double)3.0E7), y, MathHelper.clamp((double)z, (double)-3.0E7, (double)3.0E7), yaw, pit, false));
        Managers.ROTATION.setBlocking(false);
        if (((Packets)this.module).asyncTeleports.getValue().booleanValue()) {
            this.execute(x, y, z, yaw, pit, xFlag, yFlag, zFlag);
        }
        mc.addScheduledTask(() -> this.execute(x, y, z, yaw, pit, xFlag, yFlag, zFlag));
        PacketUtil.loadTerrain();
    }

    private void execute(double x, double y, double z, float yaw, float pitch, boolean xFlag, boolean yFlag, boolean zFlag) {
        if (!xFlag) {
            ListenerPosLook.mc.player.motionX = 0.0;
        }
        if (!yFlag) {
            ListenerPosLook.mc.player.motionY = 0.0;
        }
        if (!zFlag) {
            ListenerPosLook.mc.player.motionZ = 0.0;
        }
        ListenerPosLook.mc.player.setPositionAndRotation(x, y, z, yaw, pitch);
    }
}

