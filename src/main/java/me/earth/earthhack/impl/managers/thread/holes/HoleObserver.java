package me.earth.earthhack.impl.managers.thread.holes;

public interface HoleObserver
extends Comparable<HoleObserver> {
    public double getRange();

    public int getSafeHoles();

    public int getUnsafeHoles();

    public int get2x1Holes();

    public int get2x2Holes();

    @Override
    default public int compareTo(HoleObserver o) {
        return Double.compare(this.getRange(), o.getRange());
    }
}
