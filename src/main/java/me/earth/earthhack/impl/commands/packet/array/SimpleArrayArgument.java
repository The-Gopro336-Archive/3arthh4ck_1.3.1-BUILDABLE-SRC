/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.array.AbstractArrayArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class SimpleArrayArgument
extends AbstractArrayArgument<Object> {
    public SimpleArrayArgument(PacketArgument<Object> parser) {
        super(Object[].class, parser);
    }

    @Override
    protected Object[] create(int size) {
        return new Object[size];
    }

    @Override
    public Object[] fromString(String argument) throws ArgParseException {
        return SimpleArrayArgument.toArray(argument, this.parser);
    }

    public static <T> T[] toArray(String argument, PacketArgument<T> parser) throws ArgParseException {
        String[] split = argument.split("]");
        Object[] result = new Object[split.length];
        for (int i = 0; i < split.length; ++i) {
            result[i] = parser.fromString(split[i]);
        }
        return result;
    }
}

