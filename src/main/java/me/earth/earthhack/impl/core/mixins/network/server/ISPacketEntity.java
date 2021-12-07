/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketEntity
 */
package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SPacketEntity.class})
public interface ISPacketEntity {
    @Accessor(value="entityId")
    public int getEntityId();
}

