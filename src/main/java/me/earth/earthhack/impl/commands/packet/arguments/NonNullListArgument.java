/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.NonNullList
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.NonNullList;

public class NonNullListArgument
extends AbstractArgument<NonNullList> {
    public NonNullListArgument() {
        super(NonNullList.class);
    }

    @Override
    public NonNullList fromString(String argument) throws ArgParseException {
        return NonNullList.create();
    }
}

