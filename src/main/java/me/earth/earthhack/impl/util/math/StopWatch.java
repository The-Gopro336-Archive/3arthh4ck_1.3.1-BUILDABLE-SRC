/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.math;

import me.earth.earthhack.impl.util.math.Passable;

public class StopWatch
implements Passable {
    private long time;

    public boolean passed(double ms) {
        return (double)(System.currentTimeMillis() - this.time) >= ms;
    }

    @Override
    public boolean passed(long ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public StopWatch reset() {
        this.time = System.currentTimeMillis();
        return this;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.time;
    }

    public void setTime(long ns) {
        this.time = ns;
    }

    public void adjust(int time) {
        this.time += (long)time;
    }
}

