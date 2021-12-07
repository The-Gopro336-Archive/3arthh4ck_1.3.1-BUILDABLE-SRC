/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;

final class ListenerTick
extends ModuleListener<Sounds, TickEvent> {
    public ListenerTick(Sounds module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        ((Sounds)this.module).sounds.entrySet().removeIf(e -> System.currentTimeMillis() - (Long)e.getValue() > (long)((Sounds)this.module).remove.getValue().intValue());
    }
}

