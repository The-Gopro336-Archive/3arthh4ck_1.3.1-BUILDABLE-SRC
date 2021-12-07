package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.event.events.render.RenderLayersEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.Chams;

public class ListenerRenderLayers
extends ModuleListener<Chams, RenderLayersEvent> {
    public ListenerRenderLayers(Chams module) {
        super(module, RenderLayersEvent.class);
    }

    @Override
    public void invoke(RenderLayersEvent event) {
    }
}
