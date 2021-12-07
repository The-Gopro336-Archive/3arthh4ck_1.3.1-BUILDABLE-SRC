/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import java.util.Collections;
import java.util.Map;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class MapArgument
extends AbstractArgument<Map> {
    public MapArgument() {
        super(Map.class);
    }

    @Override
    public Map fromString(String argument) throws ArgParseException {
        return Collections.EMPTY_MAP;
    }
}

