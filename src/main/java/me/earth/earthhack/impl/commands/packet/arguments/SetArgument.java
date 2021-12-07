package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Collections;
import java.util.Set;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class SetArgument
extends AbstractArgument<Set> {
    public SetArgument() {
        super(Set.class);
    }

    @Override
    public Set fromString(String argument) throws ArgParseException {
        return Collections.EMPTY_SET;
    }
}
