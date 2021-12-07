package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
    public void accept(T var1) throws IOException;
}
