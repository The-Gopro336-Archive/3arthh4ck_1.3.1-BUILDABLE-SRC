/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.Chams;

public class ListenerRenderEntity
extends ModuleListener<Chams, RenderEntityEvent> {
    public ListenerRenderEntity(Chams module) {
        super(module, RenderEntityEvent.class);
    }

    @Override
    public void invoke(RenderEntityEvent event) {
    }
}

