package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

@FunctionalInterface
public interface IORunnable {
    public void run() throws IOException;
}
