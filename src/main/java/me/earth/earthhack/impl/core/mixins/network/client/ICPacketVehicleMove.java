/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketVehicleMove
 */
package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.play.client.CPacketVehicleMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketVehicleMove.class})
public interface ICPacketVehicleMove {
    @Accessor(value="y")
    public void setY(double var1);

    @Accessor(value="x")
    public void setX(double var1);

    @Accessor(value="z")
    public void setZ(double var1);
}

