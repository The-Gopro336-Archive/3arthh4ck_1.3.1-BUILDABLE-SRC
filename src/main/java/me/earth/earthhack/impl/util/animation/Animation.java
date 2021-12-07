/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.util.animation;

import me.earth.earthhack.impl.util.animation.AnimationMode;
import net.minecraft.util.math.MathHelper;

public class Animation {
    private double start;
    private double end;
    private double speed;
    private double current;
    private double last;
    private double progress = 0.0;
    private boolean backwards;
    private boolean reverseOnEnd;
    private boolean playing;
    private AnimationMode mode;

    public Animation(double start, double end, double speed, boolean backwards, AnimationMode mode) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        this.current = start;
        this.last = start;
        this.mode = mode;
        this.playing = true;
    }

    public Animation(double start, double end, double speed, boolean backwards, boolean reverseOnEnd, AnimationMode mode) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        this.reverseOnEnd = reverseOnEnd;
        this.current = start;
        this.last = start;
        this.mode = mode;
        this.playing = true;
    }

    public void add(float partialTicks) {
        if (this.playing) {
            this.last = this.current;
            if (this.mode == AnimationMode.LINEAR) {
                this.current += (double)(this.backwards ? -1 : 1) * this.speed;
            } else if (this.mode == AnimationMode.EXPONENTIAL) {
                int i = 0;
                while ((float)i < 1.0f / partialTicks) {
                    this.current += this.speed;
                    this.speed *= this.speed;
                    if (this.speed > 0.0 && this.backwards) {
                        this.speed *= -1.0;
                    }
                    ++i;
                }
            }
            this.current = MathHelper.clamp((double)this.current, (double)this.start, (double)this.end);
            if (this.current >= this.end) {
                if (this.reverseOnEnd) {
                    this.backwards = !this.backwards;
                } else {
                    this.playing = false;
                }
            }
        }
    }

    public double getStart() {
        return this.start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return this.end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCurrent() {
        return this.current;
    }

    public double getCurrent(float partialTicks) {
        return this.playing ? this.last + (this.current - this.last) * (double)partialTicks : this.current;
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

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    public boolean isReverseOnEnd() {
        return this.reverseOnEnd;
    }

    public void setReverseOnEnd(boolean reverseOnEnd) {
        this.reverseOnEnd = reverseOnEnd;
    }

    public double getProgress() {
        return this.progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}

