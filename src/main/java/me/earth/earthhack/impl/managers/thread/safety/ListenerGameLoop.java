/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.safety.SafetyManager;
import me.earth.earthhack.impl.modules.client.safety.util.Update;
import me.earth.earthhack.impl.util.math.StopWatch;

final class ListenerGameLoop
extends ModuleListener<SafetyManager, GameLoopEvent> {
    private final StopWatch timer = new StopWatch();

    public ListenerGameLoop(SafetyManager manager) {
        super(manager, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        if (((SafetyManager)this.module).mode.getValue() == Update.Fast && this.timer.passed(((SafetyManager)this.module).d.getValue().intValue())) {
            ((SafetyManager)this.module).runThread();
            this.timer.reset();
        }
    }
}

