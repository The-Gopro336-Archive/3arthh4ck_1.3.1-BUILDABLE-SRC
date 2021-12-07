/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet.generic;

import java.lang.reflect.Constructor;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;

public abstract class GenericArgument<T>
extends AbstractArgument<T> {
    private final Constructor<?> ctr;
    private final int argIndex;

    public GenericArgument(Class<? super T> type, Constructor<?> ctr, int argIndex) {
        super(type);
        this.ctr = ctr;
        this.argIndex = argIndex;
    }

    public Constructor<?> getConstructor() {
        return this.ctr;
    }

    public int getArgIndex() {
        return this.argIndex;
    }
}

