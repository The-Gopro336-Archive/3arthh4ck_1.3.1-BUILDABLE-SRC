package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SPacketPlayerPosLook.class})
public interface ISPacketPlayerPosLook {
    @Accessor(value="teleportId")
    public int getTeleportId();

    @Accessor(value="x")
    public double getX();

    @Accessor(value="y")
    public double getY();

    @Accessor(value="z")
    public double getZ();

    @Accessor(value="yaw")
    public void setYaw(float var1);

    @Accessor(value="pitch")
    public void setPitch(float var1);
}
