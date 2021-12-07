/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.arguments.AbstractEntityPlayerArgument;
import me.earth.earthhack.impl.commands.packet.util.DummyPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityPlayerArgument
extends AbstractEntityPlayerArgument<EntityPlayer> {
    public EntityPlayerArgument() {
        super(EntityPlayer.class);
    }

    @Override
    protected EntityPlayer create() {
        return new DummyPlayer((World)EntityPlayerArgument.mc.world);
    }
}

