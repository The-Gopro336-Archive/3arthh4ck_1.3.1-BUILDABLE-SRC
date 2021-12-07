/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.tweaker.launch;

import me.earth.earthhack.api.config.Jsonable;

public interface Argument<T>
extends Jsonable {
    public T getValue();
}

