package me.earth.earthhack.impl.managers.thread.holes;

import java.util.Collections;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleManager;

class HoleManager$2
extends EventListener<WorldClientEvent.Load> {
    HoleManager$2(Class target) {
        super(target);
    }

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
}
