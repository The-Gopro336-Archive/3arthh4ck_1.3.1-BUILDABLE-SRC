package me.earth.earthhack.impl.modules.client.tab;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import org.lwjgl.input.Mouse;

class TabModule$1
extends EventListener<TickEvent> {
    TabModule$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(TickEvent event) {
        if (Globals.mc.currentScreen == null && TabModule.this.isSilent) {
            Mouse.setGrabbed(false);
        }
    }
}
