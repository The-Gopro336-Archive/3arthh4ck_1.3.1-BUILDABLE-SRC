package com.formdev.flatlaf.util;

@FunctionalInterface
public interface Animator$TimingTarget {
    public void timingEvent(float var1);

    default public void begin() {
    }

    default public void end() {
    }
}
