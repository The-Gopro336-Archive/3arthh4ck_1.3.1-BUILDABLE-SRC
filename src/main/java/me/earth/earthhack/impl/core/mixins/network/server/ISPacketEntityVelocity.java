/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SPacketEntityVelocity.class})
public interface ISPacketEntityVelocity {
    @Accessor(value="entityID")
    public int getEntityID();

    @Accessor(value="motionX")
    public int getX();

    @Accessor(value="motionX")
    public void setX(int var1);

    @Accessor(value="motionY")
    public int getY();

    @Accessor(value="motionY")
    public void setY(int var1);

    @Accessor(value="motionZ")
    public int getZ();

    @Accessor(value="motionZ")
    public void setZ(int var1);
}

