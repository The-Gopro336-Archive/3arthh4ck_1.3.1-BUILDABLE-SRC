/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.NonNullList
 */
package me.earth.earthhack.impl.commands.packet.generic;

import java.lang.reflect.Constructor;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.generic.AbstractIterableArgument;
import net.minecraft.util.NonNullList;

public class GenericNonNullListArgument<T>
extends AbstractIterableArgument<T, NonNullList<T>> {
    public GenericNonNullListArgument(Constructor<?> ctr, int argIndex, PacketArgument<T> parser) {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected NonNullList<T> create(T[] array) {
        NonNullList list = NonNullList.create();
        for (T t : array) {
            if (t == null) continue;
            list.add(t);
        }
        return list;
    }
}

