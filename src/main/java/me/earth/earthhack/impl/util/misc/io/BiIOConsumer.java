package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

@FunctionalInterface
public interface BiIOConsumer<T, U> {
    public void accept(T var1, U var2) throws IOException;
}
