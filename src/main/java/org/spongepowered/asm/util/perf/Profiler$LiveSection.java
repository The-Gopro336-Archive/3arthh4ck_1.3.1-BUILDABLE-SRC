package org.spongepowered.asm.util.perf;

import java.util.Arrays;
import org.spongepowered.asm.util.perf.Profiler;

class Profiler$LiveSection
extends Profiler.Section {
    private int cursor;
    private long[] times;
    private long start;
    private long time;
    private long markedTime;
    private int count;
    private int markedCount;

    Profiler$LiveSection(String name, int cursor) {
        super(Profiler.this, name);
        this.cursor = 0;
        this.times = new long[0];
        this.start = 0L;
        this.cursor = cursor;
    }

    @Override
    Profiler.Section start() {
        this.start = System.currentTimeMillis();
        return this;
    }

    @Override
    protected Profiler.Section stop() {
        if (this.start > 0L) {
            this.time += System.currentTimeMillis() - this.start;
        }
        this.start = 0L;
        ++this.count;
        return this;
    }

    @Override
    public Profiler.Section end() {
        this.stop();
        if (!this.invalidated) {
            Profiler.this.end(this);
        }
        return this;
    }

    @Override
    void mark() {
        if (this.cursor >= this.times.length) {
            this.times = Arrays.copyOf(this.times, this.cursor + 4);
        }
        this.times[this.cursor] = this.time;
        this.markedTime += this.time;
        this.markedCount += this.count;
        this.time = 0L;
        this.count = 0;
        ++this.cursor;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getTotalTime() {
        return this.time + this.markedTime;
    }

    @Override
    public double getSeconds() {
        return (double)this.time * 0.001;
    }

    @Override
    public double getTotalSeconds() {
        return (double)(this.time + this.markedTime) * 0.001;
    }

    @Override
    public long[] getTimes() {
        long[] times = new long[this.cursor + 1];
        System.arraycopy(this.times, 0, times, 0, Math.min(this.times.length, this.cursor));
        times[this.cursor] = this.time;
        return times;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getTotalCount() {
        return this.count + this.markedCount;
    }

    @Override
    public double getAverageTime() {
        return this.count > 0 ? (double)this.time / (double)this.count : 0.0;
    }

    @Override
    public double getTotalAverageTime() {
        return this.count > 0 ? (double)(this.time + this.markedTime) / (double)(this.count + this.markedCount) : 0.0;
    }
}
