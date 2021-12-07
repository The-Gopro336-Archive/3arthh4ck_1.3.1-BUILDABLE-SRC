package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Collection;
import java.util.Collections;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class CollectionArgument
extends AbstractArgument<Collection> {
    public CollectionArgument() {
        super(Collection.class);
    }

    @Override
    public Collection fromString(String argument) throws ArgParseException {
        return Collections.EMPTY_LIST;
    }
}
