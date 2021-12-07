/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 */
package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketPlayer.class})
public interface ICPacketPlayer {
    @Accessor(value="x")
    public void setX(double var1);

    @Accessor(value="y")
    public void setY(double var1);

    @Accessor(value="z")
    public void setZ(double var1);

    @Accessor(value="yaw")
    public void setYaw(float var1);

    @Accessor(value="pitch")
    public void setPitch(float var1);

    @Accessor(value="onGround")
    public void setOnGround(boolean var1);

    @Accessor(value="moving")
    public boolean isMoving();

    @Accessor(value="rotating")
    public boolean isRotating();
}

