/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.event.bus.api;

import me.earth.earthhack.api.event.bus.api.ICancellable;
import me.earth.earthhack.api.event.bus.api.Listener;

public interface EventBus {
    public static final int DEFAULT_PRIORITY = 10;

    public void post(Object var1);

    public void post(Object var1, Class<?> var2);

    public boolean postCancellable(ICancellable var1);

    public boolean postCancellable(ICancellable var1, Class<?> var2);

    public void postReversed(Object var1, Class<?> var2);

    public void subscribe(Object var1);

    public void unsubscribe(Object var1);

    public void register(Listener<?> var1);

    public void unregister(Listener<?> var1);

    public boolean isSubscribed(Object var1);

    public boolean hasSubscribers(Class<?> var1);

    public boolean hasSubscribers(Class<?> var1, Class<?> var2);
}

