/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.reach;

import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.reach.Reach;

final class ListenerReach
extends ModuleListener<Reach, ReachEvent> {
    public ListenerReach(Reach module) {
        super(module, ReachEvent.class);
    }

    @Override
    public void invoke(ReachEvent event) {
        if (mc.getRenderViewEntity() != null && ListenerReach.mc.world != null && ListenerReach.mc.playerController != null) {
            event.setReach(((Reach)this.module).reach.getValue().floatValue());
            event.setHitBox(((Reach)this.module).hitBox.getValue().floatValue());
            event.setCancelled(true);
        }
    }
}

