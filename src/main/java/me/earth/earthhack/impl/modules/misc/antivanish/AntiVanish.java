package me.earth.earthhack.impl.modules.misc.antivanish;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.modules.misc.antivanish.AntiVanishData;
import me.earth.earthhack.impl.modules.misc.antivanish.ListenerLatency;
import me.earth.earthhack.impl.modules.misc.antivanish.util.VanishedEntry;
import me.earth.earthhack.impl.util.math.StopWatch;

public class AntiVanish
extends Module {
    protected final Map<Integer, Future<?>> futures = new ConcurrentHashMap();
    protected final Map<UUID, VanishedEntry> cache = new ConcurrentHashMap<UUID, VanishedEntry>();
    protected final AtomicInteger ids = new AtomicInteger();
    protected final StopWatch timer = new StopWatch();

    public AntiVanish() {
        super("AntiVanish", Category.Misc);
        this.listeners.add(new ListenerLatency(this));
        this.setData(new AntiVanishData(this));
    }

    @Override
    protected void onDisable() {
        for (Future<?> future : this.futures.values()) {
            if (future == null) continue;
            future.cancel(true);
        }
        this.futures.clear();
    }
}
