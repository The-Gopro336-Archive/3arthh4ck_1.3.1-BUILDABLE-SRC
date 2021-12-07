package me.earth.earthhack.impl.managers.thread.scheduler;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;

class Scheduler$1
extends EventListener<GameLoopEvent> {
    Scheduler$1(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        Scheduler.this.gameLoop = ((IMinecraft)((Object)Globals.mc)).getGameLoop();
        Scheduler.this.executing = true;
        CollectionUtil.emptyQueue(Scheduler.this.scheduled, Runnable::run);
        Scheduler.this.executing = false;
        CollectionUtil.emptyQueue(Scheduler.this.toSchedule, Scheduler.this.scheduled::add);
    }
}
