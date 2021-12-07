package me.earth.earthhack.impl.util.misc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class SkippingCounter {
    private final AtomicInteger counter;
    private final Predicate<Integer> skip;
    private final int initial;

    public SkippingCounter(int initial, Predicate<Integer> skip) {
        this.counter = new AtomicInteger(initial);
        this.initial = initial;
        this.skip = skip;
    }

    public int get() {
        return this.counter.get();
    }

    public int next() {
        int result;
        while (!this.skip.test(result = this.counter.incrementAndGet())) {
        }
        return result;
    }

    public void reset() {
        this.counter.set(this.initial);
    }
}
