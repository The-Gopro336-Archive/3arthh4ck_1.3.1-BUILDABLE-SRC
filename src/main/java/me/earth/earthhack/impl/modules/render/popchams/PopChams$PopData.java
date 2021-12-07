package me.earth.earthhack.impl.modules.render.popchams;

import net.minecraft.entity.player.EntityPlayer;

public class PopChams$PopData {
    private final EntityPlayer player;
    private final long time;
    private final float yaw;
    private final float pitch;
    private final double x;
    private final double y;
    private final double z;

    public PopChams$PopData(EntityPlayer player, long time, float yaw, float pitch, double x, double y, double z) {
        this.player = player;
        this.time = time;
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public long getTime() {
        return this.time;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
