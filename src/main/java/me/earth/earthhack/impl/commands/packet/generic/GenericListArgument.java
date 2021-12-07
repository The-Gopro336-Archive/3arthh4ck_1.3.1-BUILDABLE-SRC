/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Lists;
import java.lang.reflect.Constructor;
import java.util.List;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.generic.AbstractIterableArgument;

public class GenericListArgument<T>
extends AbstractIterableArgument<T, List<T>> {
    public GenericListArgument(Constructor<?> ctr, int argIndex, PacketArgument<T> parser) {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected List<T> create(T[] array) {
        return Lists.newArrayList((Object[])array);
    }
}

