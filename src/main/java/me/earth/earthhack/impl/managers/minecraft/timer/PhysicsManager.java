/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.PhysicsUtil;

public class PhysicsManager
extends SubscriberImpl
implements Globals {
    private final StopWatch timer = new StopWatch();
    private boolean blocking;
    private int delay;
    private int times;

    public PhysicsManager() {
        this.listeners.add(new EventListener<GameLoopEvent>(GameLoopEvent.class){

            @Override
            public void invoke(GameLoopEvent event) {
                if (Globals.mc.player == null) {
                    PhysicsManager.this.times = 0;
                    return;
                }
                if (PhysicsManager.this.times > 0 && PhysicsManager.this.timer.passed(PhysicsManager.this.delay)) {
                    PhysicsManager.this.blocking = true;
                    while (PhysicsManager.this.times > 0) {
                        PhysicsManager.this.invokePhysics();
                        if (PhysicsManager.this.delay != 0) break;
                        PhysicsManager.this.times--;
                    }
                    PhysicsManager.this.blocking = false;
                    PhysicsManager.this.timer.reset();
                }
            }
        });
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class){

            @Override
            public void invoke(DisconnectEvent event) {
                PhysicsManager.this.times = 0;
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                PhysicsManager.this.times = 0;
            }
        });
    }

    public void invokePhysics(int times, int delay) {
        if (!this.blocking) {
            this.times = times;
            this.delay = delay;
        }
    }

    public void invokePhysics() {
        PhysicsUtil.runPhysicsTick();
    }
}

