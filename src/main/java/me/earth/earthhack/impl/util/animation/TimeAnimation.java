/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.util.animation;

import me.earth.earthhack.impl.util.animation.AnimationMode;
import net.minecraft.util.math.MathHelper;

public class TimeAnimation {
    private final long length;
    private double start;
    private double end;
    private double current;
    private double progress;
    private boolean playing;
    private boolean backwards;
    private boolean reverseOnEnd;
    private long startTime;
    private long lastTime;
    private double per;
    private long dif;
    private boolean flag;
    private AnimationMode mode;

    public TimeAnimation(long length, double start, double end, boolean backwards, AnimationMode mode) {
        this.length = length;
        this.start = start;
        this.current = start;
        this.end = end;
        this.mode = mode;
        this.backwards = backwards;
        this.startTime = System.currentTimeMillis();
        this.playing = true;
        this.dif = System.currentTimeMillis() - this.startTime;
        switch (mode) {
            case LINEAR: {
                this.per = (end - start) / (double)length;
                break;
            }
            case EXPONENTIAL: {
                double dif = end - start;
                boolean bl = this.flag = dif < 0.0;
                if (this.flag) {
                    dif *= -1.0;
                }
                int i = 0;
                while ((long)i < length) {
                    dif = Math.sqrt(dif);
                    ++i;
                }
                this.per = dif;
            }
        }
        this.lastTime = System.currentTimeMillis();
    }

    public TimeAnimation(long length, double start, double end, boolean backwards, boolean reverseOnEnd, AnimationMode mode) {
        this(length, start, end, backwards, mode);
        this.reverseOnEnd = reverseOnEnd;
    }

    public void add(float partialTicks) {
        if (this.playing) {
            if (this.mode == AnimationMode.LINEAR) {
                this.current = this.start + this.progress;
                this.progress += this.per * (double)(System.currentTimeMillis() - this.lastTime);
            } else if (this.mode == AnimationMode.EXPONENTIAL) {
                // empty if block
            }
            this.current = MathHelper.clamp((double)this.current, (double)this.start, (double)this.end);
            if (this.current >= this.end || this.backwards && this.current <= this.start) {
                if (this.reverseOnEnd) {
                    this.reverse();
                    this.reverseOnEnd = false;
                } else {
                    this.playing = false;
                }
            }
        }
        this.lastTime = System.currentTimeMillis();
    }

    public long getLength() {
        return this.length;
    }

    public double getStart() {
        return this.start;
    }

    public double getEnd() {
        return this.end;
    }

    public double getCurrent() {
        return this.current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public AnimationMode getMode() {
        return this.mode;
    }

    public void setMode(AnimationMode mode) {
        this.mode = mode;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void play() {
        this.playing = true;
    }

    public void stop() {
        this.playing = false;
    }

    public boolean isBackwards() {
        return this.backwards;
    }

    public void reverse() {
        this.backwards = !this.backwards;
        this.per *= -1.0;
    }
}

