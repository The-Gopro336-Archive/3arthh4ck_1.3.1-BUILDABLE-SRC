/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagCompoundArgument
extends AbstractArgument<NBTTagCompound> {
    public NBTTagCompoundArgument() {
        super(NBTTagCompound.class);
    }

    @Override
    public NBTTagCompound fromString(String argument) throws ArgParseException {
        NBTTagCompound compound;
        try {
            compound = JsonToNBT.getTagFromJson((String)argument);
        }
        catch (NBTException e) {
            throw new ArgParseException("Couldn't parse NBT: " + e.getMessage());
        }
        return compound;
    }
}

