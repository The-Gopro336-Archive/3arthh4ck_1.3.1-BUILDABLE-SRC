package me.earth.earthhack.impl.commands.util;

import java.util.concurrent.ScheduledExecutorService;
import me.earth.earthhack.impl.util.thread.ThreadUtil;

public interface CommandScheduler {
    public static final ScheduledExecutorService SCHEDULER = ThreadUtil.newDaemonScheduledExecutor("Command");
}
