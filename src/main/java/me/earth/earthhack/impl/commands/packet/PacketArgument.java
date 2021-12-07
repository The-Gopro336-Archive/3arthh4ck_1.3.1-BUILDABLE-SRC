package me.earth.earthhack.impl.commands.packet;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public interface PacketArgument<T> {
    public T fromString(String var1) throws ArgParseException;

    public PossibleInputs getPossibleInputs(String var1);

    public CustomCompleterResult onTabComplete(Completer var1);

    public Class<? super T> getType();

    public String getSimpleName();
}
