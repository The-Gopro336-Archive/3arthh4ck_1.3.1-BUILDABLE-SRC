/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketPlayerPosLook$EnumFlags
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PositionManager
extends SubscriberImpl
implements Globals {
    private boolean blocking;
    private volatile int teleportID;
    private volatile double last_x;
    private volatile double last_y;
    private volatile double last_z;
    private volatile boolean onGround;

    public PositionManager() {
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketPlayerPosLook>>(PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketPlayerPosLook.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
                double x = packet.getX();
                double y = packet.getY();
                double z = packet.getZ();
                if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.X)) {
                    x += Globals.mc.player.posX;
                }
                if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Y)) {
                    y += Globals.mc.player.posY;
                }
                if (packet.getFlags().contains((Object)SPacketPlayerPosLook.EnumFlags.Z)) {
                    z += Globals.mc.player.posZ;
                }
                PositionManager.this.last_x = MathHelper.clamp((double)x, (double)-3.0E7, (double)3.0E7);
                PositionManager.this.last_y = y;
                PositionManager.this.last_z = MathHelper.clamp((double)z, (double)-3.0E7, (double)3.0E7);
                PositionManager.this.onGround = false;
                PositionManager.this.teleportID = packet.getTeleportId();
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.Position>>(PacketEvent.Post.class, Integer.MIN_VALUE, CPacketPlayer.Position.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Position> event) {
                PositionManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketPlayer.PositionRotation>>(PacketEvent.Post.class, Integer.MIN_VALUE, CPacketPlayer.PositionRotation.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
                PositionManager.this.readCPacket((CPacketPlayer)event.getPacket());
            }
        });
    }

    public int getTeleportID() {
        return this.teleportID;
    }

    public double getX() {
        return this.last_x;
    }

    public double getY() {
        return this.last_y;
    }

    public double getZ() {
        return this.last_z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public AxisAlignedBB getBB() {
        double x = this.last_x;
        double y = this.last_y;
        double z = this.last_z;
        float w = PositionManager.mc.player.width / 2.0f;
        float h = PositionManager.mc.player.height;
        return new AxisAlignedBB(x - (double)w, y, z - (double)w, x + (double)w, y + (double)h, z + (double)w);
    }

    public Vec3d getVec() {
        return new Vec3d(this.last_x, this.last_y, this.last_z);
    }

    public void readCPacket(CPacketPlayer packetIn) {
        this.last_x = packetIn.getX(PositionManager.mc.player.posX);
        this.last_y = packetIn.getY(PositionManager.mc.player.posY);
        this.last_z = packetIn.getZ(PositionManager.mc.player.posZ);
        this.setOnGround(packetIn.isOnGround());
    }

    public double getDistanceSq(Entity entity) {
        return this.getDistanceSq(entity.posX, entity.posY, entity.posZ);
    }

    public double getDistanceSq(double x, double y, double z) {
        double xDiff = this.last_x - x;
        double yDiff = this.last_y - y;
        double zDiff = this.last_z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    public boolean canEntityBeSeen(Entity entity) {
        return PositionManager.mc.world.rayTraceBlocks(new Vec3d(this.last_x, this.last_y + (double)PositionManager.mc.player.getEyeHeight(), this.last_z), new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
    }

    public void set(double x, double y, double z) {
        this.last_x = x;
        this.last_y = y;
        this.last_z = z;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isBlocking() {
        return this.blocking;
    }
}

