/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.thread;

import java.util.concurrent.Future;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

public class ThreadManager
implements GlobalExecutor {
    public Future<?> submit(SafeRunnable runnable) {
        return this.submitRunnable(runnable);
    }

    public Future<?> submitRunnable(Runnable runnable) {
        return EXECUTOR.submit(runnable);
    }

    public void shutDown() {
        EXECUTOR.shutdown();
    }
}

