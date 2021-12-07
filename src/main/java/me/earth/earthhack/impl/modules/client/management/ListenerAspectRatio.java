package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.render.AspectRatioEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.management.Management;

final class ListenerAspectRatio
extends ModuleListener<Management, AspectRatioEvent> {
    public ListenerAspectRatio(Management module) {
        super(module, AspectRatioEvent.class);
    }

    @Override
    public void invoke(AspectRatioEvent event) {
        if (((Management)this.module).aspectRatio.getValue().booleanValue()) {
            event.setAspectRatio((float)((Management)this.module).aspectRatioWidth.getValue().intValue() / (float)((Management)this.module).aspectRatioHeight.getValue().intValue());
        }
    }
}
