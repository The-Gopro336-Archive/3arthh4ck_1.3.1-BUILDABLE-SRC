package me.earth.earthhack.impl.util.misc;

import java.util.Objects;

public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        return o instanceof Pair && Objects.equals(this.key, ((Pair)o).key) && Objects.equals(this.value, ((Pair)o).value);
    }

    public int hashCode() {
        return 31 * Objects.hashCode(this.key) + Objects.hashCode(this.value);
    }

    public String toString() {
        return this.key + "=" + this.value;
    }
}
