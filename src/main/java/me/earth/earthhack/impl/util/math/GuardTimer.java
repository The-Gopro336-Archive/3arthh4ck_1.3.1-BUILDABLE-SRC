package me.earth.earthhack.impl.util.math;

import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.StopWatch;

public class GuardTimer
implements DiscreteTimer {
    private final StopWatch guard = new StopWatch();
    private final long interval;
    private final long guardDelay;
    private long delay;
    private long time;

    public GuardTimer() {
        this(1000L);
    }

    public GuardTimer(long guardDelay) {
        this(guardDelay, 10L);
    }

    public GuardTimer(long guardDelay, long interval) {
        this.guardDelay = guardDelay;
        this.interval = interval;
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis() - this.time;
    }

    @Override
    public boolean passed(long ms) {
        return ms == 0L || ms < this.interval || System.currentTimeMillis() - this.time >= ms;
    }

    @Override
    public DiscreteTimer reset(long ms) {
        if (ms <= this.interval || this.delay != ms || this.guard.passed(this.guardDelay)) {
            this.delay = ms;
            this.reset();
        } else {
            this.time = ms + this.time;
        }
        return this;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
        this.guard.reset();
    }
}
