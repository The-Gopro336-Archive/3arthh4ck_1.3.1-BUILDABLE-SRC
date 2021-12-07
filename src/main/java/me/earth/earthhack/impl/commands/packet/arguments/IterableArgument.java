package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Collections;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class IterableArgument
extends AbstractArgument<Iterable> {
    public IterableArgument() {
        super(Iterable.class);
    }

    @Override
    public Iterable fromString(String argument) throws ArgParseException {
        return Collections.EMPTY_LIST;
    }
}
