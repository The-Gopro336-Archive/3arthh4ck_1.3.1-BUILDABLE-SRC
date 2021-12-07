/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.render.tracers;

import java.util.ArrayList;
import java.util.Comparator;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.tracers.Tracers;
import net.minecraft.entity.Entity;

final class ListenerTick
extends ModuleListener<Tracers, TickEvent> {
    public ListenerTick(Tracers module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            ArrayList<Entity> sorted = new ArrayList<Entity>(ListenerTick.mc.world.loadedEntityList);
            try {
                sorted.sort(Comparator.comparingDouble(entity -> ListenerTick.mc.player.getDistanceSq(entity)));
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            ((Tracers)this.module).sorted = sorted;
        }
    }
}

