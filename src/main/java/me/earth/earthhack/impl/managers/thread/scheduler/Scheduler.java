package me.earth.earthhack.impl.managers.thread.scheduler;

import java.util.LinkedList;
import java.util.Queue;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;

public class Scheduler
extends SubscriberImpl
implements Globals {
    private static final Scheduler INSTANCE = new Scheduler();
    private final Queue<Runnable> scheduled = new LinkedList<Runnable>();
    private final Queue<Runnable> toSchedule = new LinkedList<Runnable>();
    private boolean executing;
    private int gameLoop;

    private Scheduler() {
        this.listeners.add(new EventListener<GameLoopEvent>(GameLoopEvent.class, Integer.MAX_VALUE){

            @Override
            public void invoke(GameLoopEvent event) {
                Scheduler.this.gameLoop = ((IMinecraft)((Object)Globals.mc)).getGameLoop();
                Scheduler.this.executing = true;
                CollectionUtil.emptyQueue(Scheduler.this.scheduled, Runnable::run);
                Scheduler.this.executing = false;
                CollectionUtil.emptyQueue(Scheduler.this.toSchedule, Scheduler.this.scheduled::add);
            }
        });
    }

    public static Scheduler getInstance() {
        return INSTANCE;
    }

    public void schedule(Runnable runnable) {
        this.schedule(runnable, true);
    }

    public void scheduleAsynchronously(Runnable runnable) {
        mc.addScheduledTask(() -> this.schedule(runnable, false));
    }

    public void schedule(Runnable runnable, boolean checkGameLoop) {
        if (mc.isCallingFromMinecraftThread()) {
            if (this.executing || checkGameLoop && this.gameLoop != ((IMinecraft)((Object)mc)).getGameLoop()) {
                this.toSchedule.add(runnable);
            } else {
                this.scheduled.add(runnable);
            }
        } else {
            mc.addScheduledTask(runnable);
        }
    }
}
