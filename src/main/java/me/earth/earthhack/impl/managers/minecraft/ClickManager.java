package me.earth.earthhack.impl.managers.minecraft;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class ClickManager {
    private final Map<Integer, Queue<Runnable>> clicks = new TreeMap<Integer, Queue<Runnable>>();

    public void scheduleClick(Runnable runnable, int priority) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void scheduleClickSynchronized(Runnable runnable, int priority) {
        Map<Integer, Queue<Runnable>> map = this.clicks;
        synchronized (map) {
            this.scheduleClick(runnable, priority);
        }
    }

    public void runClick() {
    }
}
