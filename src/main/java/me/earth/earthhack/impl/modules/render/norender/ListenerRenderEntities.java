/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.norender.NoRender;

final class ListenerRenderEntities
extends ModuleListener<NoRender, RenderEntityEvent.Pre> {
    public ListenerRenderEntities(NoRender module) {
        super(module, RenderEntityEvent.Pre.class);
    }

    @Override
    public void invoke(RenderEntityEvent.Pre event) {
        if (((NoRender)this.module).entities.getValue().booleanValue()) {
            event.setCancelled(true);
        }
    }
}

