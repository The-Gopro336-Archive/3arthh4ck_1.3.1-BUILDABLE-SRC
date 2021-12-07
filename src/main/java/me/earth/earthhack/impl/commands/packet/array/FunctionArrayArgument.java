/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet.array;

import java.util.function.Function;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.array.AbstractArrayArgument;

public class FunctionArrayArgument<T>
extends AbstractArrayArgument<T> {
    private final Function<Integer, T[]> function;

    public FunctionArrayArgument(Class<T[]> type, PacketArgument<T> parser, Function<Integer, T[]> function) {
        super(type, parser);
        this.function = function;
    }

    @Override
    protected T[] create(int size) {
        return this.function.apply(size);
    }
}

