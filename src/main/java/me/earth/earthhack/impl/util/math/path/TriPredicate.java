/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.math.path;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    public boolean test(T var1, U var2, V var3);

    default public TriPredicate<T, U, V> and(TriPredicate<? super T, ? super U, ? super V> other) {
        Objects.requireNonNull(other);
        return (t, u, v) -> this.test(t, u, v) && other.test(t, u, v);
    }

    default public TriPredicate<T, U, V> negate() {
        return (t, u, v) -> !this.test(t, u, v);
    }

    default public TriPredicate<T, U, V> or(TriPredicate<? super T, ? super U, ? super V> other) {
        Objects.requireNonNull(other);
        return (t, u, v) -> this.test(t, u, v) || other.test(t, u, v);
    }
}

