/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.api.Invoker;

public class LambdaListener<E>
extends EventListener<E> {
    private final Invoker<E> invoker;

    public LambdaListener(Class<? super E> target, Invoker<E> invoker) {
        this(target, 10, invoker);
    }

    public LambdaListener(Class<? super E> target, Class<?> type, Invoker<E> invoker) {
        this(target, 10, type, invoker);
    }

    public LambdaListener(Class<? super E> target, int priority, Invoker<E> invoker) {
        this(target, priority, null, invoker);
    }

    public LambdaListener(Class<? super E> target, int priority, Class<?> type, Invoker<E> invoker) {
        super(target, priority, type);
        this.invoker = invoker;
    }

    @Override
    public void invoke(E event) {
        this.invoker.invoke(event);
    }
}

