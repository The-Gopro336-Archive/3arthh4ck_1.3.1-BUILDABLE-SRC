/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DummyEntity
extends Entity
implements Dummy {
    public DummyEntity(World worldIn) {
        super(worldIn);
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Override
    public boolean isDummy() {
        return true;
    }
}

