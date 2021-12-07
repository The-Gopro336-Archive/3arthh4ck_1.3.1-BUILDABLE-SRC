/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class StatBaseArgument
extends AbstractArgument<StatBase> {
    public StatBaseArgument() {
        super(StatBase.class);
    }

    @Override
    public StatBase fromString(String argument) throws ArgParseException {
        return new StatBase(argument, (ITextComponent)new TextComponentString("dummy-statbase-component"));
    }
}

