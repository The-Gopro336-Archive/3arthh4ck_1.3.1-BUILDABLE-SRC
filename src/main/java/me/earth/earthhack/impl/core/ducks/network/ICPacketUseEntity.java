/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public interface ICPacketUseEntity {
    public void setEntityId(int var1);

    public void setAction(CPacketUseEntity.Action var1);

    public void setVec(Vec3d var1);

    public void setHand(EnumHand var1);

    public int getEntityID();

    public CPacketUseEntity.Action getAction();

    public Vec3d getHitVec();

    public EnumHand getHand();

    public Entity getAttackedEntity();
}

