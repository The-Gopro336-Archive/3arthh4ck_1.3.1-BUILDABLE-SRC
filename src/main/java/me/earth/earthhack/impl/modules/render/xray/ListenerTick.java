package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;

final class ListenerTick
extends ModuleListener<XRay, TickEvent> {
    private int lastOpacity = 0;

    public ListenerTick(XRay module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((XRay)this.module).getMode() == XrayMode.Opacity) {
            if (this.lastOpacity != ((XRay)this.module).getOpacity()) {
                ((XRay)this.module).loadRenderers();
                this.lastOpacity = ((XRay)this.module).getOpacity();
            }
            ListenerTick.mc.gameSettings.gammaSetting = 11.0f;
        }
    }
}
