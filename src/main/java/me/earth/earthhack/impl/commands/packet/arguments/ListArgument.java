package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Collections;
import java.util.List;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class ListArgument
extends AbstractArgument<List> {
    public ListArgument() {
        super(List.class);
    }

    @Override
    public List fromString(String argument) throws ArgParseException {
        return Collections.EMPTY_LIST;
    }
}
