package me.earth.earthhack.tweaker.launch;

import me.earth.earthhack.tweaker.launch.Argument;

public interface ArgumentManager {
    public void loadArguments();

    public void addArgument(String var1, Argument<?> var2);

    public <T> Argument<T> getArgument(String var1);
}
