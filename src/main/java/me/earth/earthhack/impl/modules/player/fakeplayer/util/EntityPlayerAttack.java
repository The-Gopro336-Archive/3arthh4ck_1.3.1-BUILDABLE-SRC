/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import com.mojang.authlib.GameProfile;
import java.util.function.BooleanSupplier;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityOtherPlayerMP;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPlayerAttack
extends EntityOtherPlayerMP
implements Globals,
IEntityOtherPlayerMP,
IEntityRemoteAttack {
    private BooleanSupplier remoteSupplier = () -> true;

    public EntityPlayerAttack(World worldIn) {
        this(worldIn, EntityPlayerAttack.mc.player.getGameProfile());
    }

    public EntityPlayerAttack(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    protected void markVelocityChanged() {
    }

    @Override
    public boolean shouldRemoteAttack() {
        return this.remoteSupplier.getAsBoolean();
    }

    @Override
    public boolean returnFromSuperAttack(DamageSource source, float amount) {
        IEntityOtherPlayerMP.super.returnFromSuperAttack(source, amount);
        return true;
    }

    @Override
    public boolean shouldAttackSuper() {
        return true;
    }

    public void setRemoteSupplier(BooleanSupplier remoteSupplier) {
        this.remoteSupplier = remoteSupplier;
    }
}

