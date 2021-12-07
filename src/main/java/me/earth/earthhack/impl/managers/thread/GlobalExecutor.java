/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.thread;

import java.util.concurrent.ExecutorService;
import me.earth.earthhack.impl.util.thread.ThreadUtil;

public interface GlobalExecutor {
    public static final ExecutorService EXECUTOR = ThreadUtil.newDaemonCachedThreadPool();
    public static final ExecutorService FIXED_EXECUTOR = ThreadUtil.newFixedThreadPool((int)((double)Runtime.getRuntime().availableProcessors() / 1.5));
}

