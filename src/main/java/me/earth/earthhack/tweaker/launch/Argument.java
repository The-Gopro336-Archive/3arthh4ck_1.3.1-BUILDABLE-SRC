package me.earth.earthhack.tweaker.launch;

import me.earth.earthhack.api.config.Jsonable;

public interface Argument<T>
extends Jsonable {
    public T getValue();
}
