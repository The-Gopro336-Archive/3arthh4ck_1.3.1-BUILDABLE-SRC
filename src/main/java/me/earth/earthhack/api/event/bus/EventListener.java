/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.event.bus;

import me.earth.earthhack.api.event.bus.api.Listener;

public abstract class EventListener<T>
implements Listener<T> {
    private final Class<? super T> target;
    private final Class<?> type;
    private final int priority;

    public EventListener(Class<? super T> target) {
        this(target, 10, null);
    }

    public EventListener(Class<? super T> target, Class<?> type) {
        this(target, 10, type);
    }

    public EventListener(Class<? super T> target, int priority) {
        this(target, priority, null);
    }

    public EventListener(Class<? super T> target, int priority, Class<?> type) {
        this.priority = priority;
        this.target = target;
        this.type = type;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public Class<? super T> getTarget() {
        return this.target;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }
}

