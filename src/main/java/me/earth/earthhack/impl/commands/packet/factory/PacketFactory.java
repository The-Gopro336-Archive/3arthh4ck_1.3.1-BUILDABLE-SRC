/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 */
package me.earth.earthhack.impl.commands.packet.factory;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import net.minecraft.network.Packet;

public interface PacketFactory {
    public Packet<?> create(Class<? extends Packet<?>> var1, String[] var2) throws ArgParseException;

    public PossibleInputs getInputs(Class<? extends Packet<?>> var1, String[] var2);

    public CustomCompleterResult onTabComplete(Completer var1);
}

