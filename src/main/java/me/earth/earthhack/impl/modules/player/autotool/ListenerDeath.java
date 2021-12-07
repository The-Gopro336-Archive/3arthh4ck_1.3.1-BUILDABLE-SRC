/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.autotool.AutoTool;

final class ListenerDeath
extends ModuleListener<AutoTool, DeathEvent> {
    public ListenerDeath(AutoTool module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (event.getEntity().equals((Object)ListenerDeath.mc.player)) {
            ((AutoTool)this.module).reset();
        }
    }
}

