/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.managers.thread.holes;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.EmptyHoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.HoleRunnable;
import me.earth.earthhack.impl.managers.thread.holes.IHoleManager;
import net.minecraft.util.math.BlockPos;

public class HoleManager
extends SubscriberImpl
implements Globals,
IHoleManager {
    private static final HoleObserver EMPTY = new EmptyHoleObserver();
    private final AtomicBoolean finished = new AtomicBoolean(true);
    private final Set<HoleObserver> observers = new HashSet<HoleObserver>();
    private List<BlockPos> safe = Collections.emptyList();
    private List<BlockPos> unsafe = Collections.emptyList();
    private List<BlockPos> longHoles = Collections.emptyList();
    private List<BlockPos> bigHoles = Collections.emptyList();

    public HoleManager() {
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
                HoleManager.this.runTick();
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void invoke(WorldClientEvent.Load event) {
                HoleManager holeManager = Managers.HOLES;
                synchronized (holeManager) {
                    HoleManager.this.safe = Collections.emptyList();
                    HoleManager.this.unsafe = Collections.emptyList();
                    HoleManager.this.longHoles = Collections.emptyList();
                    HoleManager.this.bigHoles = Collections.emptyList();
                }
            }
        });
    }

    public List<BlockPos> getSafe() {
        return this.safe;
    }

    public List<BlockPos> getUnsafe() {
        return this.unsafe;
    }

    public List<BlockPos> getLongHoles() {
        return this.longHoles;
    }

    public List<BlockPos> getBigHoles() {
        return this.bigHoles;
    }

    private void runTick() {
        if (HoleManager.mc.player != null && HoleManager.mc.world != null && this.finished.get() && !this.observers.isEmpty()) {
            double maxRange = this.getMaxRange();
            if (maxRange == 0.0) {
                return;
            }
            int safes = this.observers.stream().max(Comparator.comparing(HoleObserver::getSafeHoles)).orElse(EMPTY).getSafeHoles();
            int unsafes = this.observers.stream().max(Comparator.comparing(HoleObserver::getUnsafeHoles)).orElse(EMPTY).getSafeHoles();
            int longs = this.observers.stream().max(Comparator.comparing(HoleObserver::get2x1Holes)).orElse(EMPTY).getUnsafeHoles();
            int bigs = this.observers.stream().max(Comparator.comparing(HoleObserver::get2x2Holes)).orElse(EMPTY).getUnsafeHoles();
            if (safes != 0 || unsafes != 0 || longs != 0 || bigs != 0) {
                this.finished.set(false);
                this.calc(maxRange, safes, unsafes, longs, bigs);
            }
        }
    }

    protected void calc(double maxRange, int safes, int unsafes, int longs, int bigs) {
        Managers.THREAD.submit(new HoleRunnable(this, maxRange, safes, unsafes, longs, bigs));
    }

    @Override
    public void setSafe(List<BlockPos> safe) {
        this.safe = safe;
    }

    @Override
    public void setUnsafe(List<BlockPos> unsafe) {
        this.unsafe = unsafe;
    }

    @Override
    public void setLongHoles(List<BlockPos> longHoles) {
        this.longHoles = longHoles;
    }

    @Override
    public void setBigHoles(List<BlockPos> bigHoles) {
        this.bigHoles = bigHoles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFinished() {
        this.finished.set(true);
        HoleManager holeManager = this;
        synchronized (holeManager) {
            this.notifyAll();
        }
    }

    public boolean register(HoleObserver observer) {
        this.observers.add(observer);
        if (this.observers.size() == 1) {
            this.runTick();
            return true;
        }
        return false;
    }

    public void unregister(HoleObserver observer) {
        this.observers.remove(observer);
    }

    public boolean isFinished() {
        return this.finished.get();
    }

    public double getMaxRange() {
        if (this.observers.isEmpty()) {
            return 0.0;
        }
        try {
            return Collections.max(this.observers).getRange();
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

