/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Lists;
import java.lang.reflect.Constructor;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.generic.AbstractIterableArgument;

public class GenericIterableArgument<T>
extends AbstractIterableArgument<T, Iterable<T>> {
    public GenericIterableArgument(Constructor<?> ctr, int argIndex, PacketArgument<T> parser) {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected Iterable<T> create(T[] array) {
        return Lists.newArrayList((Object[])array);
    }
}

