package me.earth.earthhack.impl.util.math;

public class Timer {
    private long startTime = System.currentTimeMillis();

    public long getTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public void adjust(int by) {
        this.startTime += (long)by;
    }
}
