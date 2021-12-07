/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.cache;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Cache<T> {
    protected Supplier<T> getter;
    protected boolean frozen;
    protected T cached;

    protected Cache() {
        this.getter = () -> null;
    }

    public Cache(T value) {
        this.getter = () -> value;
        this.cached = value;
    }

    public Cache(Supplier<T> getter) {
        this.getter = getter;
    }

    public Cache<T> set(T value) {
        this.cached = value;
        return this;
    }

    public T get() {
        if (this.isPresent()) {
            return this.cached;
        }
        return null;
    }

    public boolean isPresent() {
        if (this.cached == null && !this.frozen) {
            this.cached = this.getter.get();
        }
        return this.cached != null;
    }

    public boolean computeIfPresent(Consumer<T> consumer) {
        if (this.isPresent()) {
            consumer.accept(this.cached);
            return true;
        }
        return false;
    }

    public <E> E returnIfPresent(Function<T, E> function, E defaultValue) {
        if (this.isPresent()) {
            return function.apply(this.cached);
        }
        return defaultValue;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}

