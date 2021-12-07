package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

final class PositionHistoryHelper$RotationHistory {
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float pitch;
    public final long time;
    public final AxisAlignedBB bb;

    public PositionHistoryHelper$RotationHistory(CPacketPlayer packet) {
        this(packet.getX(Managers.POSITION.getX()), packet.getY(Managers.POSITION.getY()), packet.getZ(Managers.POSITION.getZ()), packet.getYaw(Managers.ROTATION.getServerYaw()), packet.getPitch(Managers.ROTATION.getServerPitch()));
    }

    public PositionHistoryHelper$RotationHistory(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.time = System.currentTimeMillis();
        float w = Globals.mc.player.width / 2.0f;
        float h = Globals.mc.player.height;
        this.bb = new AxisAlignedBB(x - (double)w, y, z - (double)w, x + (double)w, y + (double)h, z + (double)w);
    }
}
