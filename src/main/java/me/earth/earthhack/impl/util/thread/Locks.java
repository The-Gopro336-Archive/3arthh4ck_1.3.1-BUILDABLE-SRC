/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {
    public static final Lock PLACE_SWITCH_LOCK = new ReentrantLock();
    public static final Lock WINDOW_CLICK_LOCK = new ReentrantLock();

    public static void acquire(Lock lock, Runnable runnable) {
        try {
            lock.lock();
            runnable.run();
        }
        finally {
            lock.unlock();
        }
    }

    public static Runnable wrap(Lock lock, Runnable runnable) {
        return () -> Locks.acquire(lock, runnable);
    }
}

