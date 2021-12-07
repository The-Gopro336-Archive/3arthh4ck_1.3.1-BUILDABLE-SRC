/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.commands.packet.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DummyPlayer
extends EntityPlayer
implements Dummy {
    public DummyPlayer(World worldIn) {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Dummy-Player"));
    }

    public boolean isSpectator() {
        return false;
    }

    public boolean isCreative() {
        return false;
    }
}

