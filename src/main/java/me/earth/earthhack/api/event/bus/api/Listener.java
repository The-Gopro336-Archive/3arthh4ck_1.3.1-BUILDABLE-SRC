/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.event.bus.api;

import me.earth.earthhack.api.event.bus.api.Invoker;

public interface Listener<T>
extends Invoker<T> {
    public int getPriority();

    public Class<? super T> getTarget();

    public Class<?> getType();
}

