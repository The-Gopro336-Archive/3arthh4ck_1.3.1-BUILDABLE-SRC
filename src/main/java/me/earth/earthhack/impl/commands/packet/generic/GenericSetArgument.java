package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.util.Set;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.generic.AbstractIterableArgument;

public class GenericSetArgument<T>
extends AbstractIterableArgument<T, Set<T>> {
    public GenericSetArgument(Constructor<?> ctr, int argIndex, PacketArgument<T> parser) {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected Set<T> create(T[] array) {
        return Sets.newHashSet(array);
    }
}
